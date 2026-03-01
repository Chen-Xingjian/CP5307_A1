package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    planId: Long,
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    val selected by vm.selectedPlan.collectAsState()
    val form by vm.form.collectAsState()

    var editing by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(planId) { vm.bindPlanById(planId) }
    LaunchedEffect(selected?.id) { selected?.let { vm.loadToForm(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.planDetailTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } },
                actions = {
                    TextButton(
                        onClick = {
                            editing = !editing
                            if (!editing) expanded = false
                        }
                    ) { Text(if (editing) s.cancel else s.edit) }
                }
            )
        }
    ) { inner ->
        if (selected == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ===== Detail Card =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Name
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = vm::onNameChange,
                        label = { Text(s.planName) },
                        enabled = editing, // ✅ 未编辑虚化；编辑黑框
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category dropdown
                    Box {
                        OutlinedTextField(
                            value = form.selectedType.localizedName(isZh),
                            onValueChange = {},
                            readOnly = true,
                            enabled = editing, // ✅ 未编辑虚化；编辑黑框
                            label = { Text(s.category) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (editing) Modifier.clickable { expanded = true }
                                    else Modifier
                                )
                        )

                        DropdownMenu(
                            expanded = expanded && editing,
                            onDismissRequest = { expanded = false }
                        ) {
                            form.categoryOptions.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.localizedName(isZh)) },
                                    onClick = {
                                        vm.onTypeChange(t)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Duration + Calories row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = form.durationMinutes,
                            onValueChange = vm::onDurationChange,
                            label = { Text(s.durationMinutes) },
                            enabled = editing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = form.estimatedCalories,
                            onValueChange = vm::onCaloriesChange,
                            label = { Text(s.estimatedCalories) },
                            enabled = editing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Note
                    OutlinedTextField(
                        value = form.note,
                        onValueChange = vm::onNoteChange,
                        label = { Text(s.planNote) },
                        enabled = editing,
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error
                    form.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ===== Bottom Save (only when editing) =====
            if (editing) {
                Button(
                    onClick = { vm.updatePlan { editing = false; expanded = false } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(s.saveChanges, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}