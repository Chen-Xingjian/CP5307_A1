package au.edu.jcu.fittrackplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TypeFilter { ALL, APPLIED, NOT_APPLIED }

data class TypeManageUiState(
    val filter: TypeFilter = TypeFilter.ALL,
    val map: Map<WorkoutType, Boolean> = emptyMap(),
    val list: List<Pair<WorkoutType, Boolean>> = emptyList()
)

@HiltViewModel
class WorkoutTypeManageViewModel @Inject constructor(
    private val repo: FitTrackRepository
) : ViewModel() {

    private val filter = MutableStateFlow(TypeFilter.ALL)

    val ui: StateFlow<TypeManageUiState> =
        combine(repo.observeAllWorkoutTypeSettings(), filter) { map, f ->
            val raw = map.toList().sortedBy { it.first.name }
            val filtered = when (f) {
                TypeFilter.ALL -> raw
                TypeFilter.APPLIED -> raw.filter { it.second }
                TypeFilter.NOT_APPLIED -> raw.filter { !it.second }
            }
            TypeManageUiState(filter = f, map = map, list = filtered)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TypeManageUiState())

    fun initIfNeeded() {
        viewModelScope.launch { repo.initWorkoutTypeSettingsIfEmpty() }
    }

    fun setFilter(f: TypeFilter) {
        filter.value = f
    }

    fun toggle(type: WorkoutType, enabled: Boolean) {
        viewModelScope.launch { repo.setWorkoutTypeEnabled(type, enabled) }
    }
}