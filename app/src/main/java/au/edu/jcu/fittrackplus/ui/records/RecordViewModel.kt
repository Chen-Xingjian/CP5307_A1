package au.edu.jcu.fittrackplus.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import au.edu.jcu.fittrackplus.domain.util.CalorieCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

data class RecordUiState(
    val selectedType: WorkoutType = WorkoutType.RUNNING,
    val note: String = "",
    val startTimeMillis: Long? = null,
    val endTimeMillis: Long? = null,
    val elapsedSeconds: Long = 0L,
    val isRunning: Boolean = false,
    val canSave: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RecordUiState())
    val ui: StateFlow<RecordUiState> = _ui.asStateFlow()

    private var profileCache: UserProfile? = null

    init {
        viewModelScope.launch {
            repository.observeUserProfile().collect { profileCache = it }
        }
    }

    fun onTypeChange(type: WorkoutType) {
        _ui.update { it.copy(selectedType = type) }
    }

    fun onNoteChange(note: String) {
        _ui.update { it.copy(note = note) }
    }

    fun startWorkout() {
        val now = System.currentTimeMillis()
        _ui.update {
            it.copy(
                startTimeMillis = now,
                endTimeMillis = null,
                elapsedSeconds = 0L,
                isRunning = true,
                canSave = false,
                message = null
            )
        }
    }

    fun endWorkout() {
        val start = _ui.value.startTimeMillis ?: return
        val end = System.currentTimeMillis()
        val elapsedSec = max(1L, (end - start) / 1000L)
        _ui.update {
            it.copy(
                endTimeMillis = end,
                elapsedSeconds = elapsedSec,
                isRunning = false,
                canSave = true
            )
        }
    }

    /** 给 UI 的 LaunchedEffect 每秒调用一次 */
    fun tickNow() {
        val start = _ui.value.startTimeMillis ?: return
        if (_ui.value.isRunning) {
            val now = System.currentTimeMillis()
            _ui.update { it.copy(elapsedSeconds = max(1L, (now - start) / 1000L)) }
        }
    }

    fun saveRecord() {
        val state = _ui.value
        val start = state.startTimeMillis ?: return
        val end = state.endTimeMillis ?: System.currentTimeMillis()

        val durationMinutes = max(1, ((end - start) / 1000L / 60L).toInt())
        val calories = CalorieCalculator.calculate(
            type = state.selectedType,
            durationMinutes = durationMinutes,
            profile = profileCache
        )

        val record = WorkoutRecord(
            workoutType = state.selectedType,
            startTimeMillis = start,
            endTimeMillis = end,
            durationMinutes = durationMinutes,
            calories = calories,
            note = state.note
        )

        viewModelScope.launch {
            repository.addRecord(record)
            // 保存后保留类型，重置表单
            _ui.value = RecordUiState(
                selectedType = state.selectedType,
                message = "Workout record saved."
            )
        }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null) }
    }
}