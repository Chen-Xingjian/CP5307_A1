package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCreateScreen(
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Plan") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
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
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = form.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    form.categoryOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                vm.onCategoryChange(it)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = form.durationMinutes,
                onValueChange = vm::onDurationChange,
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.estimatedCalories,
                onValueChange = vm::onCaloriesChange,
                label = { Text("Estimated Calories") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.note,
                onValueChange = vm::onNoteChange,
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            form.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { vm.createPlan(onSuccess = onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}