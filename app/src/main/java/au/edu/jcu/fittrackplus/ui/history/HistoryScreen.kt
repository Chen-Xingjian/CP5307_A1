package au.edu.jcu.fittrackplus.ui.history

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
fun HistoryContent(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(ui.filteredRecords) { r ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${r.workoutType.displayName} • ${r.durationMinutes} min • ${r.calories} kcal")
                    if (r.note.isNotBlank()) Text("Note: ${r.note}")
                }
            }
        }
    }
}