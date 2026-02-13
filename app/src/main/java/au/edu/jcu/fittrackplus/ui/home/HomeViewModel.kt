package au.edu.jcu.fittrackplus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedCategory: String = "Running",
    val categoryOptions: List<String> = listOf("Running", "Cycling", "Swimming", "Strength"),
    val inputMinutes: String = "",          // 用户可输入目标分钟；空=正计时
    val isRunning: Boolean = false,
    val elapsedSeconds: Long = 0L,          // 正计时用
    val remainingSeconds: Long? = null      // 倒计时用，null=非倒计时
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = value)
    }

    fun onInputMinutesChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(inputMinutes = filtered)
    }

    fun onQuickStartClick() {
        if (_uiState.value.isRunning) return

        val minutes = _uiState.value.inputMinutes.toLongOrNull()
        val countdownSeconds = if (minutes != null && minutes > 0) minutes * 60 else null

        _uiState.value = _uiState.value.copy(
            isRunning = true,
            elapsedSeconds = 0L,
            remainingSeconds = countdownSeconds
        )

        startTicker()
    }

    fun onStopClick() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun onResetClick() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            elapsedSeconds = 0L,
            remainingSeconds = _uiState.value.inputMinutes.toLongOrNull()?.times(60)
        )
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val s = _uiState.value

                if (s.remainingSeconds != null) {
                    val next = s.remainingSeconds - 1
                    if (next <= 0) {
                        _uiState.value = s.copy(
                            remainingSeconds = 0L,
                            isRunning = false
                        )
                        break
                    } else {
                        _uiState.value = s.copy(remainingSeconds = next)
                    }
                } else {
                    _uiState.value = s.copy(elapsedSeconds = s.elapsedSeconds + 1)
                }
            }
        }
    }

    fun displayTime(): String {
        val s = _uiState.value
        val seconds = s.remainingSeconds ?: s.elapsedSeconds
        val mm = (seconds / 60).toString().padStart(2, '0')
        val ss = (seconds % 60).toString().padStart(2, '0')
        return "$mm:$ss"
    }
}