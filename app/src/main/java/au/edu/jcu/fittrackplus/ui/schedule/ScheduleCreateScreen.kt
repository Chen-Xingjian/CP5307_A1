package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = form.name,
                onValueChange = vm::onNameChange,
                label = { Text(s.planName) },
                modifier = Modifier.fillMaxWidth()
            )

            // Category dropdown：WorkoutType + 本地化显示
            Box {
                OutlinedTextField(
                    value = form.selectedType.localizedName(isZh),
                    onValueChange = {},
                    readOnly = true,
                    enabled = true, // ✅ 关键：不要 disabled，否则会变灰框
                    label = { Text(s.category) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 覆盖层接管点击
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

            OutlinedTextField(
                value = form.durationMinutes,
                onValueChange = vm::onDurationChange,
                label = { Text(s.durationMinutes) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.estimatedCalories,
                onValueChange = vm::onCaloriesChange,
                label = { Text(s.estimatedCalories) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.note,
                onValueChange = vm::onNoteChange,
                label = { Text(s.planNote) },
                modifier = Modifier.fillMaxWidth()
            )

            form.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { vm.createPlan(onSuccess = onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(s.save)
            }
        }
    }
}