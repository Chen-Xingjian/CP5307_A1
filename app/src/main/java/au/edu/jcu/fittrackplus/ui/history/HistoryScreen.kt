package au.edu.jcu.fittrackplus.ui.history

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.ui.components.FitTrackCard
import au.edu.jcu.fittrackplus.ui.components.FitTrackScreen
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    var typeExpanded by rememberSaveable { mutableStateOf(false) }

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            ctx,
            { _, year, month, dayOfMonth ->
                val key = year * 10000 + (month + 1) * 100 + dayOfMonth
                viewModel.setDayFilter(key)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.historyTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } },
                actions = {
                    if (ui.selectedCount > 0) {
                        TextButton(onClick = viewModel::deleteSelected) {
                            Text("${s.delete}(${ui.selectedCount})")
                        }
                    }
                    TextButton(onClick = viewModel::resetFilters) { Text(s.reset) }
                }
            )
        }
    ) { inner ->
        FitTrackScreen(
            modifier = Modifier
                .padding(inner)
        ) {
            // ===== Filters Card =====
            FitTrackCard {
                Text(
                    text = s.filter,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ---- Type filter ----
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = ui.selectedType?.localizedName(isZh) ?: s.selectAll,
                            onValueChange = {},
                            readOnly = true,
                            enabled = true, // ✅ keep normal border
                            label = { Text(s.type) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { typeExpanded = true }
                        )

                        DropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(s.selectAll) },
                                onClick = {
                                    viewModel.setTypeFilter(null)
                                    typeExpanded = false
                                }
                            )
                            ui.availableTypes.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.localizedName(isZh)) },
                                    onClick = {
                                        viewModel.setTypeFilter(t)
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // ---- Date filter ----
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = ui.selectedDayKey?.let { formatDayKey(it) } ?: s.selectAll,
                            onValueChange = {},
                            readOnly = true,
                            enabled = true, // ✅ keep normal border
                            label = { Text(s.date) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { openDatePicker() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ===== Empty / List =====
            if (ui.filteredRecords.isEmpty()) {
                FitTrackCard {
                    Text(
                        text = s.noRecords,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                return@FitTrackScreen
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
                items(items = ui.filteredRecords, key = { it.id }) { record ->
                    RecordSelectableCard(
                        record = record,
                        selected = ui.selectedIds.contains(record.id),
                        onToggle = { viewModel.toggleSelection(record.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordSelectableCard(
    record: WorkoutRecord,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    val timeFmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    val startTime = timeFmt.format(Date(record.startTimeMillis))

    val container = if (selected) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Title line (primary)
            Text(
                text = record.workoutType.localizedName(isZh),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Metrics line (secondary)
            Text(
                text = "${formatHHmmss(record.durationSeconds)} • ${record.calories} kcal",
                style = MaterialTheme.typography.bodyMedium
            )

            // Start time (small)
            Text(
                text = "${s.startLabel}: $startTime",
                style = MaterialTheme.typography.bodySmall
            )

            if (record.note.isNotBlank()) {
                Text(
                    text = "${s.noteLabel}: ${record.note}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (selected) {
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.selected,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

private fun formatDayKey(dayKey: Int): String {
    val y = dayKey / 10000
    val m = (dayKey / 100) % 100
    val d = dayKey % 100
    return "%04d-%02d-%02d".format(y, m, d)
}

private fun formatHHmmss(totalSeconds: Long): String {
    val sec = totalSeconds.coerceAtLeast(0)
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}