package au.edu.jcu.fittrackplus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import au.edu.jcu.fittrackplus.domain.util.CalorieCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WorkoutPhase { IDLE, RUNNING, PAUSED, STOPPED }

data class HomeWorkoutUiState(
    val categoryOptions: List<WorkoutType> = listOf(
        WorkoutType.RUNNING,
        WorkoutType.WALKING,
        WorkoutType.CYCLING,
        WorkoutType.SWIMMING,
        WorkoutType.STRENGTH
    ),
    val selectedCategory: WorkoutType = WorkoutType.RUNNING,

    val targetHourText: String = "",
    val targetMinuteText: String = "",

    val phase: WorkoutPhase = WorkoutPhase.IDLE,
    val startTimeMillis: Long? = null,
    val endTimeMillis: Long? = null,

    val elapsedSeconds: Long = 0L,
    val remainingSeconds: Long? = null,

    // "SAVED" / "TIME_UP" / null
    val lastSavedMessage: String? = null
) {
    val isIdle: Boolean get() = phase == WorkoutPhase.IDLE
    val isRunning: Boolean get() = phase == WorkoutPhase.RUNNING
    val isPaused: Boolean get() = phase == WorkoutPhase.PAUSED
    val isStopped: Boolean get() = phase == WorkoutPhase.STOPPED

    val canControl: Boolean get() = phase == WorkoutPhase.RUNNING || phase == WorkoutPhase.PAUSED
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeWorkoutUiState())
    val ui: StateFlow<HomeWorkoutUiState> = _ui.asStateFlow()

    private var ticker: Job? = null

    init {
        viewModelScope.launch { repository.initWorkoutTypeSettingsIfEmpty() }

        viewModelScope.launch {
            repository.observeEnabledWorkoutTypes().collect { enabled ->
                if (enabled.isEmpty()) return@collect
                _ui.update { s ->
                    val newSelected =
                        if (enabled.contains(s.selectedCategory)) s.selectedCategory else enabled.first()
                    s.copy(categoryOptions = enabled, selectedCategory = newSelected)
                }
            }
        }
    }

    fun onCategoryChange(type: WorkoutType) {
        _ui.update { it.copy(selectedCategory = type) }
    }

    fun onTargetHourChange(input: String) {
        val filtered = input.filter { it.isDigit() }.take(2)
        val hh = filtered.toIntOrNull()
        val clamped = when {
            filtered.isEmpty() -> ""
            hh == null -> ""
            hh > 23 -> "23"
            else -> filtered
        }
        _ui.update { it.copy(targetHourText = clamped) }
    }

    fun onTargetMinuteChange(input: String) {
        val filtered = input.filter { it.isDigit() }.take(2)
        val mm = filtered.toIntOrNull()
        val clamped = when {
            filtered.isEmpty() -> ""
            mm == null -> ""
            mm > 59 -> "59"
            else -> filtered
        }
        _ui.update { it.copy(targetMinuteText = clamped) }
    }

    /**
     * ✅ 从计划启动：预设类型 + 预设时长（分钟）并立即开始。
     * 注意：这里直接进入 RUNNING，不依赖 IDLE 判断。
     */
    fun startFromPlan(typeName: String, minutes: Int) {
        if (minutes <= 0) return

        val type = WorkoutType.fromName(typeName)

        // 先停止旧 ticker，避免旧 session 干扰
        stopTicker()

        val hh = (minutes / 60).coerceAtMost(23)
        val mm = (minutes % 60).coerceAtMost(59)

        val start = System.currentTimeMillis()
        val remaining = (hh * 3600L + mm * 60L).takeIf { it > 0 }

        _ui.update { s ->
            s.copy(
                selectedCategory = type,
                targetHourText = hh.toString(),
                targetMinuteText = mm.toString().padStart(2, '0'),

                phase = WorkoutPhase.RUNNING,
                startTimeMillis = start,
                endTimeMillis = null,
                elapsedSeconds = 0L,
                remainingSeconds = remaining,

                lastSavedMessage = null
            )
        }

        startTicker()
    }

    /**
     * Quick start（手动开始）
     */
    fun startQuick() {
        val state = _ui.value
        if (!state.isIdle) return

        val start = System.currentTimeMillis()
        val remaining = parseTargetSeconds(state.targetHourText, state.targetMinuteText)

        _ui.value = state.copy(
            phase = WorkoutPhase.RUNNING,
            startTimeMillis = start,
            endTimeMillis = null,
            elapsedSeconds = 0L,
            remainingSeconds = remaining,
            lastSavedMessage = null
        )

        startTicker()
    }

    fun pause() {
        if (!_ui.value.isRunning) return
        stopTicker()
        _ui.update { it.copy(phase = WorkoutPhase.PAUSED) }
    }

    fun resume() {
        if (!_ui.value.isPaused) return
        _ui.update { it.copy(phase = WorkoutPhase.RUNNING) }
        startTicker()
    }

    /**
     * ✅ 给 NavGraph 离开弹窗用：更稳的 pause/resume（只对 RUNNING/PAUSED 生效）
     */
    fun pauseForLeave() {
        val s = _ui.value
        if (s.phase == WorkoutPhase.RUNNING) {
            stopTicker()
            _ui.update { it.copy(phase = WorkoutPhase.PAUSED) }
        }
    }

    fun resumeForLeave() {
        val s = _ui.value
        if (s.phase == WorkoutPhase.PAUSED) {
            _ui.update { it.copy(phase = WorkoutPhase.RUNNING) }
            startTicker()
        }
    }

    /** 手动停止：不提示 TIME_UP */
    fun stop() {
        val s = _ui.value
        if (s.phase != WorkoutPhase.RUNNING && s.phase != WorkoutPhase.PAUSED) return
        stopTicker()
        _ui.update { it.copy(phase = WorkoutPhase.STOPPED, endTimeMillis = System.currentTimeMillis()) }
    }

    /** 倒计时结束自动停止：提示 TIME_UP */
    private fun stopFromTimer() {
        val s = _ui.value
        if (s.phase != WorkoutPhase.RUNNING) return
        stopTicker()
        _ui.update {
            it.copy(
                phase = WorkoutPhase.STOPPED,
                endTimeMillis = System.currentTimeMillis(),
                lastSavedMessage = "TIME_UP"
            )
        }
    }

    /**
     * ✅ 保存并重置到 IDLE
     */
    fun saveAndReset() {
        val state = _ui.value
        val start = state.startTimeMillis ?: return
        val end = state.endTimeMillis ?: System.currentTimeMillis()
        val durationSec = state.elapsedSeconds.coerceAtLeast(1L)

        viewModelScope.launch {
            val profile = repository.observeUserProfile().first()
            val calories = CalorieCalculator.calculateSeconds(
                type = state.selectedCategory,
                durationSeconds = durationSec,
                profile = profile
            )

            repository.addRecord(
                WorkoutRecord(
                    id = 0L,
                    workoutType = state.selectedCategory,
                    startTimeMillis = start,
                    endTimeMillis = end,
                    durationSeconds = durationSec,
                    calories = calories,
                    note = ""
                )
            )

            // ✅ 重置必须彻底清掉 session 字段
            _ui.value = HomeWorkoutUiState(
                categoryOptions = state.categoryOptions,
                selectedCategory = state.selectedCategory,
                targetHourText = "",
                targetMinuteText = "",
                phase = WorkoutPhase.IDLE,
                startTimeMillis = null,
                endTimeMillis = null,
                elapsedSeconds = 0L,
                remainingSeconds = null,
                lastSavedMessage = "SAVED"
            )
        }
    }

    fun discardAndReset() {
        stopTicker()
        _ui.value = HomeWorkoutUiState(
            categoryOptions = _ui.value.categoryOptions,
            selectedCategory = _ui.value.selectedCategory,
            targetHourText = "",
            targetMinuteText = "",
            phase = WorkoutPhase.IDLE,
            startTimeMillis = null,
            endTimeMillis = null,
            elapsedSeconds = 0L,
            remainingSeconds = null,
            lastSavedMessage = null
        )
    }

    fun clearToast() {
        _ui.update { it.copy(lastSavedMessage = null) }
    }

    private fun startTicker() {
        stopTicker()
        ticker = viewModelScope.launch {
            while (true) {
                delay(1000)
                val s = _ui.value
                if (s.phase != WorkoutPhase.RUNNING) continue

                val newElapsed = s.elapsedSeconds + 1

                if (s.remainingSeconds != null) {
                    val newRemaining = (s.remainingSeconds - 1).coerceAtLeast(0)
                    _ui.update { it.copy(elapsedSeconds = newElapsed, remainingSeconds = newRemaining) }
                    if (newRemaining <= 0) {
                        stopFromTimer()
                        break
                    }
                } else {
                    _ui.update { it.copy(elapsedSeconds = newElapsed) }
                }
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }

    private fun parseTargetSeconds(hourText: String, minuteText: String): Long? {
        val hh = hourText.toIntOrNull() ?: 0
        val mm = minuteText.toIntOrNull() ?: 0
        val totalSeconds = hh * 3600L + mm * 60L
        return if (totalSeconds > 0) totalSeconds else null
    }
}