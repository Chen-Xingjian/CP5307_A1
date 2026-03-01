package au.edu.jcu.fittrackplus.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * UI state for the History screen.
 *
 * - [selectedType] and [selectedDayKey] are nullable to represent "Select All".
 * - [selectedIds] stores the set of record IDs selected for bulk actions (e.g., delete).
 */
data class HistoryUiState(
    val filteredRecords: List<WorkoutRecord> = emptyList(),
    val availableTypes: List<WorkoutType> = emptyList(),
    val selectedType: WorkoutType? = null,    // null = Select All
    val selectedDayKey: Int? = null,          // null = Select All (yyyyMMdd)
    val selectedIds: Set<Long> = emptySet()
) {
    /** Number of currently selected records. */
    val selectedCount: Int get() = selectedIds.size
}

/**
 * ViewModel for the History screen.
 *
 * Responsibilities:
 * - Observe records from the repository.
 * - Build available filter options from existing records.
 * - Apply type/date filters.
 * - Manage multi-selection and bulk deletion.
 *
 * Note: This ViewModel only coordinates state; formatting and rendering are done in the UI layer.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    // Current filter state (null means "Select All").
    private val selectedType = MutableStateFlow<WorkoutType?>(null)
    private val selectedDayKey = MutableStateFlow<Int?>(null)

    // IDs of records selected for bulk operations.
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())

    /**
     * Combined UI state driven by:
     * - All records (from repository)
     * - Current filters (type/date)
     * - Current selection set
     */
    val ui: StateFlow<HistoryUiState> =
        combine(
            repository.observeAllRecords(),
            selectedType,
            selectedDayKey,
            selectedIds
        ) { all, type, dayKey, ids ->
            // Ensure most recent records appear first.
            val sorted = all.sortedByDescending { it.startTimeMillis }

            // Derive available workout types from existing records.
            val types = sorted.map { it.workoutType }.distinct().sortedBy { it.name }

            // Apply filters (type and/or day).
            val filtered = sorted.filter { r ->
                val okType = type == null || r.workoutType == type
                val okDay = dayKey == null || r.dayKey() == dayKey
                okType && okDay
            }

            HistoryUiState(
                filteredRecords = filtered,
                availableTypes = types,
                selectedType = type,
                selectedDayKey = dayKey,
                selectedIds = ids
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    /** Updates the workout type filter. Use null to clear (Select All). */
    fun setTypeFilter(type: WorkoutType?) {
        selectedType.value = type
    }

    /** Updates the day filter using a yyyyMMdd integer key. Use null to clear (Select All). */
    fun setDayFilter(dayKey: Int?) {
        selectedDayKey.value = dayKey
    }

    /** Toggles selection state for a given record ID. */
    fun toggleSelection(id: Long) {
        val cur = selectedIds.value
        selectedIds.value = if (cur.contains(id)) cur - id else cur + id
    }

    /** Clears all currently selected record IDs. */
    fun clearSelection() {
        selectedIds.value = emptySet()
    }

    /**
     * Deletes all currently selected records.
     *
     * Note: Deletion is performed sequentially via the repository.
     * After deletion, the selection set is cleared.
     */
    fun deleteSelected() {
        val ids = selectedIds.value.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            ids.forEach { repository.deleteRecordById(it) }
            selectedIds.value = emptySet()
        }
    }

    /**
     * Resets filters back to "Select All".
     *
     * Also clears selection to reduce the risk of accidental deletion after a reset.
     */
    fun resetFilters() {
        selectedType.value = null
        selectedDayKey.value = null
        selectedIds.value = emptySet()
    }
}

/**
 * Computes an integer day key (yyyyMMdd) from the record's [WorkoutRecord.startTimeMillis].
 *
 * This is used to support exact-day filtering without relying on string parsing.
 */
private fun WorkoutRecord.dayKey(): Int {
    val cal = Calendar.getInstance().apply { timeInMillis = startTimeMillis }
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return y * 10000 + m * 100 + d
}