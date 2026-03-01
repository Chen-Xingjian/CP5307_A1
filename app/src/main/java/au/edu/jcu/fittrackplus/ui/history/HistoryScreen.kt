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

/**
 * History screen showing workout records with optional filters:
 * - Workout type (dropdown)
 * - Date (date picker)
 *
 * Users can select records and perform bulk actions (delete/reset filters) via the top app bar.
 *
 * Note:
 * - Filtering logic is handled by [HistoryViewModel].
 * - This composable only renders UI and forwards user intents to the view model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    // Screen state from ViewModel (lifecycle-aware).
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    val ctx = LocalContext.current
    var typeExpanded by rememberSaveable { mutableStateOf(false) }

    /**
     * Opens a native DatePicker dialog and writes the selected day into ViewModel as `yyyyMMdd` int.
     */
    fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            ctx,
            { _, year, month, dayOfMonth ->
                // Encode date as an integer key for easy comparison/filtering.
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
                    // Show bulk delete only when user has selected records.
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
            modifier = Modifier.padding(inner)
        ) {
            // ===== Filters =====
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
                    // ---- Type filter (dropdown) ----
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = ui.selectedType?.localizedName(isZh) ?: s.selectAll,
                            onValueChange = {},
                            readOnly = true,
                            // Keep enabled for a normal border style; click is handled by overlay box.
                            enabled = true,
                            label = { Text(s.type) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Transparent overlay to capture taps without making the field editable.
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

                    // ---- Date filter (native date picker) ----
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = ui.selectedDayKey?.let { formatDayKey(it) } ?: s.selectAll,
                            onValueChange = {},
                            readOnly = true,
                            // Keep enabled for a normal border style; click is handled by overlay box.
                            enabled = true,
                            label = { Text(s.date) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Transparent overlay to open the date picker.
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { openDatePicker() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ===== Empty state / Records list =====
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

/**
 * A selectable record card used in [HistoryScreen].
 *
 * Selection state is controlled by the parent (ViewModel) and toggled via [onToggle].
 */
@Composable
private fun RecordSelectableCard(
    record: WorkoutRecord,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    // Format start time for display (device locale).
    val timeFmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    val startTime = timeFmt.format(Date(record.startTimeMillis))

    val containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Primary title: localized workout type.
            Text(
                text = record.workoutType.localizedName(isZh),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Secondary metrics: duration + calories.
            Text(
                text = "${formatHHmmss(record.durationSeconds)} • ${record.calories} kcal",
                style = MaterialTheme.typography.bodyMedium
            )

            // Start time label.
            Text(
                text = "${s.startLabel}: $startTime",
                style = MaterialTheme.typography.bodySmall
            )

            // Optional note.
            if (record.note.isNotBlank()) {
                Text(
                    text = "${s.noteLabel}: ${record.note}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Selection hint.
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

/**
 * Converts an integer day key (yyyyMMdd) into a formatted date string (yyyy-MM-dd).
 */
private fun formatDayKey(dayKey: Int): String {
    val y = dayKey / 10000
    val m = (dayKey / 100) % 100
    val d = dayKey % 100
    return "%04d-%02d-%02d".format(y, m, d)
}

/**
 * Formats duration seconds into HH:mm:ss.
 */
private fun formatHHmmss(totalSeconds: Long): String {
    val sec = totalSeconds.coerceAtLeast(0)
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}