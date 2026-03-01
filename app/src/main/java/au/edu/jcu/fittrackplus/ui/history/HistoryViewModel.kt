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

data class HistoryUiState(
    val filteredRecords: List<WorkoutRecord> = emptyList(),
    val availableTypes: List<WorkoutType> = emptyList(),
    val selectedType: WorkoutType? = null,    // null = Select All
    val selectedDayKey: Int? = null,          // null = Select All (yyyyMMdd)
    val selectedIds: Set<Long> = emptySet()
) {
    val selectedCount: Int get() = selectedIds.size
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val selectedType = MutableStateFlow<WorkoutType?>(null)
    private val selectedDayKey = MutableStateFlow<Int?>(null)
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())

    val ui: StateFlow<HistoryUiState> =
        combine(
            repository.observeAllRecords(),
            selectedType,
            selectedDayKey,
            selectedIds
        ) { all, type, dayKey, ids ->
            val sorted = all.sortedByDescending { it.startTimeMillis }

            val types = sorted.map { it.workoutType }.distinct().sortedBy { it.name }

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

    fun setTypeFilter(type: WorkoutType?) {
        selectedType.value = type
    }

    fun setDayFilter(dayKey: Int?) {
        selectedDayKey.value = dayKey
    }

    fun toggleSelection(id: Long) {
        val cur = selectedIds.value
        selectedIds.value = if (cur.contains(id)) cur - id else cur + id
    }

    fun clearSelection() {
        selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        val ids = selectedIds.value.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            ids.forEach { repository.deleteRecordById(it) }
            selectedIds.value = emptySet()
        }
    }

    /** Reset 只重置筛选框到 Select All（我也清空选中，避免误删） */
    fun resetFilters() {
        selectedType.value = null
        selectedDayKey.value = null
        selectedIds.value = emptySet()
    }
}

private fun WorkoutRecord.dayKey(): Int {
    val cal = Calendar.getInstance().apply { timeInMillis = startTimeMillis }
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return y * 10000 + m * 100 + d
}