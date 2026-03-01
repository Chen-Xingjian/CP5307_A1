package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCreateScreen(
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh
    val form by vm.form.collectAsState()

    var expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.newPlanTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ===== Main Form Card =====
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

                    // Plan name
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = vm::onNameChange,
                        label = { Text(s.planName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category dropdown (keep enabled=true for normal border)
                    Box {
                        OutlinedTextField(
                            value = form.selectedType.localizedName(isZh),
                            onValueChange = {},
                            readOnly = true,
                            enabled = true, // keep normal border
                            label = { Text(s.category) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Click overlay
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expanded = true }
                        )

                        DropdownMenu(
                            expanded = expanded,
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

                    // Duration + Calories in one row (more app-like)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = form.durationMinutes,
                            onValueChange = vm::onDurationChange,
                            label = { Text(s.durationMinutes) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = form.estimatedCalories,
                            onValueChange = vm::onCaloriesChange,
                            label = { Text(s.estimatedCalories) },
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

            // ===== Bottom Primary Button =====
            Button(
                onClick = { vm.createPlan(onSuccess = onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(s.save, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}