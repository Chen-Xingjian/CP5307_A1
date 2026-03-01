package au.edu.jcu.fittrackplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Filter options for the Workout Type Management screen.
 */
enum class TypeFilter {
    /** Show all workout types. */
    ALL,

    /** Show only enabled workout types. */
    APPLIED,

    /** Show only disabled workout types. */
    NOT_APPLIED
}

/**
 * UI state for [WorkoutTypeManageScreen].
 *
 * @property filter Current list filter.
 * @property map Full mapping of workout type -> enabled state.
 * @property list Filtered list derived from [map] and [filter], ready for UI rendering.
 */
data class TypeManageUiState(
    val filter: TypeFilter = TypeFilter.ALL,
    val map: Map<WorkoutType, Boolean> = emptyMap(),
    val list: List<Pair<WorkoutType, Boolean>> = emptyList()
)

/**
 * ViewModel for Workout Type Management.
 *
 * Responsibilities:
 * - Observes persisted workout type settings from [FitTrackRepository].
 * - Exposes a filtered, UI-ready list based on the current [TypeFilter].
 * - Writes enable/disable updates back to the repository.
 *
 * Notes:
 * - Default settings initialization is performed via [initIfNeeded].
 * - UI filtering is purely presentation logic and does not affect persisted data.
 */
@HiltViewModel
class WorkoutTypeManageViewModel @Inject constructor(
    private val repo: FitTrackRepository
) : ViewModel() {

    /**
     * Current filter selection for the UI.
     */
    private val filter = MutableStateFlow(TypeFilter.ALL)

    /**
     * Combined UI state derived from persisted settings and the current filter.
     */
    val ui: StateFlow<TypeManageUiState> =
        combine(repo.observeAllWorkoutTypeSettings(), filter) { map, f ->
            // Stable ordering for consistent UI rendering.
            val raw = map.toList().sortedBy { it.first.name }

            // Apply the selected filter without mutating the underlying data.
            val filtered = when (f) {
                TypeFilter.ALL -> raw
                TypeFilter.APPLIED -> raw.filter { it.second }
                TypeFilter.NOT_APPLIED -> raw.filter { !it.second }
            }

            TypeManageUiState(
                filter = f,
                map = map,
                list = filtered
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TypeManageUiState()
        )

    /**
     * Ensures default workout type settings exist in storage.
     *
     * This should be called once when the screen is first shown.
     */
    fun initIfNeeded() {
        viewModelScope.launch { repo.initWorkoutTypeSettingsIfEmpty() }
    }

    /**
     * Updates the current UI filter.
     *
     * @param f New filter value.
     */
    fun setFilter(f: TypeFilter) {
        filter.value = f
    }

    /**
     * Persists a toggle for a workout type.
     *
     * @param type The target workout type.
     * @param enabled The new enabled state.
     */
    fun toggle(type: WorkoutType, enabled: Boolean) {
        viewModelScope.launch { repo.setWorkoutTypeEnabled(type, enabled) }
    }
}