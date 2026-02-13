package au.edu.jcu.fittrackplus.ui.record

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordContent(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(ui.isRunning) {
        while (ui.isRunning) {
            delay(1000L)
            viewModel.tickNow()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = ui.selectedType.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Workout Type") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                WorkoutType.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.displayName) },
                        onClick = {
                            viewModel.onTypeChange(it)
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = ui.note,
            onValueChange = viewModel::onNoteChange,
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Elapsed: ${ui.elapsedSeconds}s")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::startWorkout, enabled = !ui.isRunning) { Text("Start") }
            Button(onClick = viewModel::endWorkout, enabled = ui.isRunning) { Text("End") }
            Button(onClick = { viewModel.saveRecord()}, enabled = !ui.isRunning) { Text("Save") }
        }

        ui.message?.let { Text(it) }
    }
}