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

/**
 * Represents the runtime phase of a workout session on the Home screen.
 */
enum class WorkoutPhase {
    /** No active workout; user can configure and start a session. */
    IDLE,

    /** Workout session is running and time is progressing. */
    RUNNING,

    /** Workout session is paused; time does not progress. */
    PAUSED,

    /** Workout session is stopped; user can save or discard. */
    STOPPED
}

/**
 * UI state for Home workout flow.
 *
 * Notes:
 * - The session is "active" for navigation blocking only when [phase] is RUNNING or PAUSED.
 * - [remainingSeconds] is non-null when running in countdown mode; otherwise the session counts up.
 * - [lastSavedMessage] is a lightweight event key consumed by the UI layer (e.g., snackbar).
 */
data class HomeWorkoutUiState(
    /** Enabled workout type options (driven by user settings). */
    val categoryOptions: List<WorkoutType> = listOf(
        WorkoutType.RUNNING,
        WorkoutType.WALKING,
        WorkoutType.CYCLING,
        WorkoutType.SWIMMING,
        WorkoutType.STRENGTH
    ),

    /** Currently selected workout type. */
    val selectedCategory: WorkoutType = WorkoutType.RUNNING,

    /** Target hours input as text (countdown configuration). */
    val targetHourText: String = "",

    /** Target minutes input as text (countdown configuration). */
    val targetMinuteText: String = "",

    /** Current workout phase. */
    val phase: WorkoutPhase = WorkoutPhase.IDLE,

    /** Session start timestamp (milliseconds since epoch). */
    val startTimeMillis: Long? = null,

    /** Session end timestamp (milliseconds since epoch). */
    val endTimeMillis: Long? = null,

    /** Elapsed session time in seconds (count-up). */
    val elapsedSeconds: Long = 0L,

    /** Remaining time in seconds (countdown); null means count-up mode. */
    val remainingSeconds: Long? = null,

    /**
     * One-shot UI message key:
     * - "SAVED": record persisted successfully
     * - "TIME_UP": countdown ended automatically
     * - null: no message
     */
    val lastSavedMessage: String? = null
) {
    /** Convenience flag for IDLE phase. */
    val isIdle: Boolean get() = phase == WorkoutPhase.IDLE

    /** Convenience flag for RUNNING phase. */
    val isRunning: Boolean get() = phase == WorkoutPhase.RUNNING

    /** Convenience flag for PAUSED phase. */
    val isPaused: Boolean get() = phase == WorkoutPhase.PAUSED

    /** Convenience flag for STOPPED phase. */
    val isStopped: Boolean get() = phase == WorkoutPhase.STOPPED

    /**
     * Whether the user can control the session (pause/resume/stop).
     * STOPPED is not controllable; only save/discard applies.
     */
    val canControl: Boolean get() = phase == WorkoutPhase.RUNNING || phase == WorkoutPhase.PAUSED
}

/**
 * ViewModel for Home workout flow.
 *
 * Responsibilities:
 * - Maintain workout session state and timer ticker.
 * - Persist workout records via [FitTrackRepository].
 * - Provide entry points for quick start and plan-based start.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    /** Internal mutable UI state. */
    private val _ui = MutableStateFlow(HomeWorkoutUiState())

    /** Public immutable UI state. */
    val ui: StateFlow<HomeWorkoutUiState> = _ui.asStateFlow()

    /** Timer job that ticks once per second while in RUNNING phase. */
    private var ticker: Job? = null

    init {
        // Ensure default workout type settings exist in the database.
        viewModelScope.launch { repository.initWorkoutTypeSettingsIfEmpty() }

        // Keep Home category options in sync with "enabled workout types" configuration.
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

    /**
     * Updates the selected workout category.
     */
    fun onCategoryChange(type: WorkoutType) {
        _ui.update { it.copy(selectedCategory = type) }
    }

    /**
     * Updates the target hour input.
     *
     * Rules:
     * - Digits only, max 2 chars.
     * - Clamped to [0, 23] when parseable.
     */
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

    /**
     * Updates the target minute input.
     *
     * Rules:
     * - Digits only, max 2 chars.
     * - Clamped to [0, 59] when parseable.
     */
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
     * Starts a workout session from a plan preset.
     *
     * This entry point:
     * - Sets the workout type based on [typeName] (expects WorkoutType.name).
     * - Converts [minutes] into HH/MM inputs.
     * - Enters RUNNING phase immediately (does not require IDLE).
     * - Cancels any existing ticker first to avoid cross-session interference.
     */
    fun startFromPlan(typeName: String, minutes: Int) {
        if (minutes <= 0) return

        val type = WorkoutType.fromName(typeName)

        // Stop any previous session ticker to avoid leaking ticks into the new session.
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
     * Starts a workout session from the current Home configuration.
     *
     * Behavior:
     * - Only starts from IDLE (to prevent accidental re-start during a session).
     * - Enters countdown mode if target HH/MM yields a positive total seconds.
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

    /**
     * Pauses a running workout session.
     */
    fun pause() {
        if (!_ui.value.isRunning) return
        stopTicker()
        _ui.update { it.copy(phase = WorkoutPhase.PAUSED) }
    }

    /**
     * Resumes a paused workout session.
     */
    fun resume() {
        if (!_ui.value.isPaused) return
        _ui.update { it.copy(phase = WorkoutPhase.RUNNING) }
        startTicker()
    }

    /**
     * Pauses the workout in response to a navigation leave-confirm dialog.
     *
     * This is intentionally more defensive than [pause] to align with
     * the NavGraph's "pause while dialog is visible" behavior.
     */
    fun pauseForLeave() {
        val s = _ui.value
        if (s.phase == WorkoutPhase.RUNNING) {
            stopTicker()
            _ui.update { it.copy(phase = WorkoutPhase.PAUSED) }
        }
    }

    /**
     * Resumes the workout after a navigation leave-confirm dialog is dismissed.
     */
    fun resumeForLeave() {
        val s = _ui.value
        if (s.phase == WorkoutPhase.PAUSED) {
            _ui.update { it.copy(phase = WorkoutPhase.RUNNING) }
            startTicker()
        }
    }

    /**
     * Stops the current workout session manually.
     *
     * This does not emit "TIME_UP" because the stop action is user initiated.
     */
    fun stop() {
        val s = _ui.value
        if (s.phase != WorkoutPhase.RUNNING && s.phase != WorkoutPhase.PAUSED) return
        stopTicker()
        _ui.update { it.copy(phase = WorkoutPhase.STOPPED, endTimeMillis = System.currentTimeMillis()) }
    }

    /**
     * Stops the workout session due to countdown completion.
     *
     * Emits the "TIME_UP" event key for UI display (e.g., snackbar).
     */
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
     * Persists the current workout record and resets state to IDLE.
     *
     * Steps:
     * 1) Compute calories using second-level precision.
     * 2) Insert a [WorkoutRecord] into storage.
     * 3) Reset all session fields and emit the "SAVED" message key.
     *
     * Note:
     * - This function is a no-op if the session was never started (startTimeMillis is null).
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

            // Fully reset session state to avoid plan auto-start or stale fields affecting subsequent runs.
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

    /**
     * Discards the current session and returns to IDLE without persisting a record.
     */
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

    /**
     * Clears the one-shot UI message key after it has been consumed by the UI.
     */
    fun clearToast() {
        _ui.update { it.copy(lastSavedMessage = null) }
    }

    /**
     * Starts the timer ticker loop.
     *
     * The loop:
     * - Ticks every second while in RUNNING phase.
     * - Updates elapsed time always.
     * - Updates remaining time when in countdown mode and stops when it reaches 0.
     */
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

    /**
     * Cancels the current ticker job if it exists.
     */
    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }

    /**
     * Parses target hour/minute input into total countdown seconds.
     *
     * Returns null when the total is <= 0, which indicates count-up mode.
     */
    private fun parseTargetSeconds(hourText: String, minuteText: String): Long? {
        val hh = hourText.toIntOrNull() ?: 0
        val mm = minuteText.toIntOrNull() ?: 0
        val totalSeconds = hh * 3600L + mm * 60L
        return if (totalSeconds > 0) totalSeconds else null
    }
}