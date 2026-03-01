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

data class PlanFormState(
    val id: Long = 0L,
    val name: String = "",

    // 用稳定标识：WorkoutType（而不是显示文本）
    val selectedType: WorkoutType = WorkoutType.RUNNING,
    val categoryOptions: List<WorkoutType> = WorkoutType.entries.toList(),

    val durationMinutes: String = "",
    val estimatedCalories: String = "",
    val note: String = "",

    // 自动计算：用户手动改 calorie 后就关闭自动覆盖
    val caloriesAuto: Boolean = true,

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

    init {
        // 运动种类管理联动：只展示“已启用”的类型
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
                    recomputeCaloriesIfAuto()
                }
        }
    }

    // ===== 列表：单条删除（对应 UI 的 Delete 按钮）=====
    fun deletePlan(id: Long) {
        viewModelScope.launch {
            repository.deletePlanById(id)
        }
    }

    // ===== 表单：输入 =====
    fun onNameChange(v: String) {
        _form.update { it.copy(name = v, error = null) }
    }

    fun onTypeChange(t: WorkoutType) {
        _form.update { it.copy(selectedType = t, error = null) }
        recomputeCaloriesIfAuto()
    }

    fun onDurationChange(v: String) {
        val filtered = v.filter(Char::isDigit).take(4)
        _form.update { it.copy(durationMinutes = filtered, error = null) }
        recomputeCaloriesIfAuto()
    }

    // 用户手动改卡路里 => 关闭自动覆盖
    fun onCaloriesChange(v: String) {
        val filtered = v.filter(Char::isDigit).take(6)
        _form.update { it.copy(estimatedCalories = filtered, caloriesAuto = false, error = null) }
    }

    fun onNoteChange(v: String) {
        _form.update { it.copy(note = v, error = null) }
    }

    fun resetForm() {
        val cur = _form.value
        _form.value = PlanFormState(
            selectedType = cur.selectedType,
            categoryOptions = cur.categoryOptions
        )
        recomputeCaloriesIfAuto()
    }

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
            caloriesAuto = false, // 详情页默认不自动覆盖已有值
            error = null
        )
    }

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
                    category = f.selectedType.name, // ✅ 存 enum name
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

    fun bindPlanById(id: Long) {
        viewModelScope.launch {
            repository.observePlanById(id).collect { plan ->
                selectedPlan.value = plan
            }
        }
    }

    // ===== 自动计算 kcal（分钟 -> 秒 -> calculateSeconds）=====
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

    private suspend fun computeCaloriesInt(type: WorkoutType, durationMinutes: Int): Int {
        val profile = repository.observeUserProfile().first()
        val minutes = durationMinutes.coerceAtLeast(1)
        val seconds = minutes.toLong() * 60L
        return CalorieCalculator.calculateSeconds(type, seconds, profile)
    }
}