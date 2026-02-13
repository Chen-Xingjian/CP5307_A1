package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    planId: Long,
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val selected by vm.selectedPlan.collectAsState()
    val form by vm.form.collectAsState()
    var editing by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(planId) { vm.bindPlanById(planId) }
    LaunchedEffect(selected?.id) {
        selected?.let { vm.loadToForm(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan Detail") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = {
                    TextButton(onClick = { editing = !editing }) {
                        Text(if (editing) "Cancel" else "Edit")
                    }
                }
            )
        }
    ) { inner ->
        if (selected == null) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

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
                enabled = editing,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded && editing,
                onExpandedChange = { if (editing) expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = form.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    enabled = editing,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded && editing) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded && editing,
                    onDismissRequest = { expanded = false }
                ) {
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
                enabled = editing,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.estimatedCalories,
                onValueChange = vm::onCaloriesChange,
                label = { Text("Estimated Calories") },
                enabled = editing,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.note,
                onValueChange = vm::onNoteChange,
                label = { Text("Note") },
                enabled = editing,
                modifier = Modifier.fillMaxWidth()
            )

            form.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (editing) {
                Button(
                    onClick = {
                        vm.updatePlan {
                            editing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}