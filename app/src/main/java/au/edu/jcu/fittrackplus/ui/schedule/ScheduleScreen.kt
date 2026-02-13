package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScheduleContent(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Schedule planner")

        OutlinedTextField(
            value = ui.form.note,
            onValueChange = viewModel::setNote,
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = viewModel::saveAppointment) { Text("Save Schedule") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(ui.appointments) { a ->
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        "${a.workoutType.displayName} - ${a.scheduledTimeMillis}",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}