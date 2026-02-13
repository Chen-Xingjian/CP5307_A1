package au.edu.jcu.fittrackplus.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanFormState(
    val id: Long = 0L,
    val name: String = "",
    val category: String = "Running",
    val durationMinutes: String = "",
    val estimatedCalories: String = "",
    val note: String = "",
    val categoryOptions: List<String> = listOf("Running", "Cycling", "Swimming", "Strength"),
    val error: String? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    val plans: StateFlow<List<WorkoutPlan>> =
        repository.observeAllPlans()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _form = MutableStateFlow(PlanFormState())
    val form: StateFlow<PlanFormState> = _form.asStateFlow()

    val selectedPlan: MutableStateFlow<WorkoutPlan?> = MutableStateFlow(null)

    fun onNameChange(v: String) { _form.update { it.copy(name = v, error = null) } }
    fun onCategoryChange(v: String) { _form.update { it.copy(category = v, error = null) } }
    fun onDurationChange(v: String) { _form.update { it.copy(durationMinutes = v.filter(Char::isDigit), error = null) } }
    fun onCaloriesChange(v: String) { _form.update { it.copy(estimatedCalories = v.filter(Char::isDigit), error = null) } }
    fun onNoteChange(v: String) { _form.update { it.copy(note = v, error = null) } }

    fun resetForm() { _form.value = PlanFormState() }

    fun loadToForm(plan: WorkoutPlan) {
        selectedPlan.value = plan
        _form.value = PlanFormState(
            id = plan.id,
            name = plan.name,
            category = plan.category,
            durationMinutes = plan.durationMinutes.toString(),
            estimatedCalories = plan.estimatedCalories.toString(),
            note = plan.note
        )
    }

    fun createPlan(onSuccess: () -> Unit) {
        val f = _form.value
        val duration = f.durationMinutes.toIntOrNull()
        val kcal = f.estimatedCalories.toIntOrNull()

        if (f.name.isBlank() || duration == null || kcal == null) {
            _form.update { it.copy(error = "Please complete required fields.") }
            return
        }

        viewModelScope.launch {
            repository.addPlan(
                WorkoutPlan(
                    name = f.name,
                    category = f.category,
                    durationMinutes = duration,
                    estimatedCalories = kcal,
                    note = f.note
                )
            )
            resetForm()
            onSuccess()
        }
    }

    fun updatePlan(onSuccess: () -> Unit) {
        val f = _form.value
        val duration = f.durationMinutes.toIntOrNull()
        val kcal = f.estimatedCalories.toIntOrNull()

        if (f.id <= 0 || f.name.isBlank() || duration == null || kcal == null) {
            _form.update { it.copy(error = "Invalid plan data.") }
            return
        }

        viewModelScope.launch {
            repository.updatePlan(
                WorkoutPlan(
                    id = f.id,
                    name = f.name,
                    category = f.category,
                    durationMinutes = duration,
                    estimatedCalories = kcal,
                    note = f.note,
                    createdAt = selectedPlan.value?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            onSuccess()
        }
    }

    fun bindPlanById(id: Long) {
        viewModelScope.launch {
            repository.observePlanById(id).collect { plan ->
                selectedPlan.value = plan
            }
        }
    }
}