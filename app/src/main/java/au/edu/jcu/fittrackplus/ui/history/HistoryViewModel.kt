package au.edu.jcu.fittrackplus.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HistoryUiState(
    val allRecords: List<WorkoutRecord> = emptyList(),
    val filteredRecords: List<WorkoutRecord> = emptyList(),
    val selectedType: WorkoutType? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: FitTrackRepository
) : ViewModel() {

    private val selectedTypeFlow = MutableStateFlow<WorkoutType?>(null)

    val ui: StateFlow<HistoryUiState> =
        combine(
            repository.observeAllRecords(),
            selectedTypeFlow
        ) { all, selectedType ->
            val filtered = if (selectedType == null) {
                all
            } else {
                all.filter { it.workoutType == selectedType }
            }

            HistoryUiState(
                allRecords = all,
                filteredRecords = filtered,
                selectedType = selectedType
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState()
        )

    fun filterByType(type: WorkoutType?) {
        selectedTypeFlow.value = type
    }

    fun resetFilter() {
        selectedTypeFlow.value = null
    }
}