package au.edu.jcu.fittrackplus.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import au.edu.jcu.fittrackplus.domain.util.CalorieCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI form state for creating/editing a workout plan.
 *
 * Notes:
 * - `selectedType` uses a stable enum identifier (WorkoutType) instead of display text.
 * - `caloriesAuto` is turned off once the user manually edits the calories field.
 */
data class PlanFormState(
    val id: Long = 0L,
    val name: String = "",

    /** Stable identifier for the selected workout type. */
    val selectedType: WorkoutType = WorkoutType.RUNNING,

    /** Available workout types (usually restricted to enabled types from settings). */
    val categoryOptions: List<WorkoutType> = WorkoutType.entries.toList(),

    /** Minutes input as text to support partial/invalid user input gracefully. */
    val durationMinutes: String = "",

    /** Calories input as text to support partial/invalid user input gracefully. */
    val estimatedCalories: String = "",

    val note: String = "",

    /**
     * When true, calories will be recomputed automatically when type/duration changes.
     * When the user edits calories manually, this becomes false to avoid overwriting user input.
     */
    val caloriesAuto: Boolean = true,

    /** Validation or operation error message for the form. */
    val error: String? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    /**
     * All workout plans, sorted by repository implementation.
     * Exposed as StateFlow for Compose consumption.
     */
    val plans: StateFlow<List<WorkoutPlan>> =
        repository.observeAllPlans()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(PlanFormState())
    val form: StateFlow<PlanFormState> = _form.asStateFlow()

    /**
     * The plan currently bound to the detail screen (nullable until loaded).
     */
    val selectedPlan: MutableStateFlow<WorkoutPlan?> = MutableStateFlow(null)

    init {
        // Keep the plan type options in sync with "enabled workout types" from Settings.
        viewModelScope.launch {
            runCatching { repository.observeEnabledWorkoutTypes() }
                .getOrNull()
                ?.collect { enabled ->
                    if (enabled.isEmpty()) return@collect

                    _form.update { f ->
                        val newSelected =
                            if (enabled.contains(f.selectedType)) f.selectedType else enabled.first()
                        f.copy(categoryOptions = enabled, selectedType = newSelected)
                    }

                    // Recompute calories only when auto mode is still enabled.
                    recomputeCaloriesIfAuto()
                }
        }
    }

    // ---------- List actions ----------

    /**
     * Deletes a single plan (used by the list screen "Delete" action).
     */
    fun deletePlan(id: Long) {
        viewModelScope.launch {
            repository.deletePlanById(id)
        }
    }

    // ---------- Form input handlers ----------

    /**
     * Updates plan name and clears any previous error.
     */
    fun onNameChange(v: String) {
        _form.update { it.copy(name = v, error = null) }
    }

    /**
     * Updates the selected workout type and recomputes calories if auto mode is on.
     */
    fun onTypeChange(t: WorkoutType) {
        _form.update { it.copy(selectedType = t, error = null) }
        recomputeCaloriesIfAuto()
    }

    /**
     * Updates duration text (digits only) and recomputes calories if auto mode is on.
     */
    fun onDurationChange(v: String) {
        val filtered = v.filter(Char::isDigit).take(4)
        _form.update { it.copy(durationMinutes = filtered, error = null) }
        recomputeCaloriesIfAuto()
    }

    /**
     * Updates calories text (digits only) and disables auto mode to preserve user input.
     */
    fun onCaloriesChange(v: String) {
        val filtered = v.filter(Char::isDigit).take(6)
        _form.update { it.copy(estimatedCalories = filtered, caloriesAuto = false, error = null) }
    }

    /**
     * Updates note text and clears any previous error.
     */
    fun onNoteChange(v: String) {
        _form.update { it.copy(note = v, error = null) }
    }

    /**
     * Resets the form to a clean state while preserving the current type options and selection.
     */
    fun resetForm() {
        val cur = _form.value
        _form.value = PlanFormState(
            selectedType = cur.selectedType,
            categoryOptions = cur.categoryOptions
        )
        recomputeCaloriesIfAuto()
    }

    /**
     * Loads a plan into the editable form state.
     *
     * Notes:
     * - Calories auto mode is disabled by default in detail editing to avoid unexpected overrides.
     */
    fun loadToForm(plan: WorkoutPlan) {
        selectedPlan.value = plan
        val type = WorkoutType.fromName(plan.category)

        _form.value = _form.value.copy(
            id = plan.id,
            name = plan.name,
            selectedType = type,
            durationMinutes = plan.durationMinutes.toString(),
            estimatedCalories = plan.estimatedCalories.toString(),
            note = plan.note,
            caloriesAuto = false,
            error = null
        )
    }

    // ---------- Create / update ----------

    /**
     * Validates input and creates a new workout plan.
     *
     * @param onSuccess Callback invoked after the plan is successfully created.
     */
    fun createPlan(onSuccess: () -> Unit) {
        val f = _form.value
        val durationMin = f.durationMinutes.toIntOrNull()?.takeIf { it > 0 }
        val kcalInput = f.estimatedCalories.toIntOrNull()?.takeIf { it > 0 }

        if (f.name.isBlank() || durationMin == null) {
            _form.update { it.copy(error = "Please complete required fields.") }
            return
        }

        viewModelScope.launch {
            val kcal = kcalInput ?: computeCaloriesInt(f.selectedType, durationMin)

            repository.addPlan(
                WorkoutPlan(
                    id = 0L,
                    name = f.name.trim(),
                    category = f.selectedType.name, // Persist enum name as stable identifier.
                    durationMinutes = durationMin,
                    estimatedCalories = kcal,
                    note = f.note.trim(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            resetForm()
            onSuccess()
        }
    }

    /**
     * Validates input and updates an existing workout plan.
     *
     * @param onSuccess Callback invoked after the plan is successfully updated.
     */
    fun updatePlan(onSuccess: () -> Unit) {
        val f = _form.value
        val durationMin = f.durationMinutes.toIntOrNull()?.takeIf { it > 0 }
        val kcalInput = f.estimatedCalories.toIntOrNull()?.takeIf { it > 0 }

        if (f.id <= 0 || f.name.isBlank() || durationMin == null) {
            _form.update { it.copy(error = "Invalid plan data.") }
            return
        }

        viewModelScope.launch {
            val kcal = kcalInput ?: computeCaloriesInt(f.selectedType, durationMin)

            repository.updatePlan(
                WorkoutPlan(
                    id = f.id,
                    name = f.name.trim(),
                    category = f.selectedType.name,
                    durationMinutes = durationMin,
                    estimatedCalories = kcal,
                    note = f.note.trim(),
                    createdAt = selectedPlan.value?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            onSuccess()
        }
    }

    /**
     * Starts observing a plan by id and updates [selectedPlan] whenever it changes.
     */
    fun bindPlanById(id: Long) {
        viewModelScope.launch {
            repository.observePlanById(id).collect { plan ->
                selectedPlan.value = plan
            }
        }
    }

    // ---------- Helpers ----------

    /**
     * Recomputes calories only if [PlanFormState.caloriesAuto] is true.
     * Clears calories if duration is missing/invalid.
     */
    private fun recomputeCaloriesIfAuto() {
        val f = _form.value
        if (!f.caloriesAuto) return

        val durationMin = f.durationMinutes.toIntOrNull()?.takeIf { it > 0 } ?: run {
            _form.update { it.copy(estimatedCalories = "") }
            return
        }

        viewModelScope.launch {
            val kcal = computeCaloriesInt(f.selectedType, durationMin)
            _form.update { it.copy(estimatedCalories = kcal.toString()) }
        }
    }

    /**
     * Computes estimated calories for a plan using the user's profile and a seconds-based calculator.
     */
    private suspend fun computeCaloriesInt(type: WorkoutType, durationMinutes: Int): Int {
        val profile = repository.observeUserProfile().first()
        val minutes = durationMinutes.coerceAtLeast(1)
        val seconds = minutes.toLong() * 60L
        return CalorieCalculator.calculateSeconds(type, seconds, profile)
    }
}