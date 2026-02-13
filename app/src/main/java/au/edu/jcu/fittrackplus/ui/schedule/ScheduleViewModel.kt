package au.edu.jcu.fittrackplus.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleFormState(
    val type: WorkoutType = WorkoutType.RUNNING,
    val scheduledTimeMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val message: String? = null
)

data class ScheduleUiState(
    val form: ScheduleFormState = ScheduleFormState(),
    val appointments: List<Appointment> = emptyList()
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val formFlow = MutableStateFlow(ScheduleFormState())

    val ui: StateFlow<ScheduleUiState> =
        combine(
            formFlow,
            repository.observeAppointments()
        ) { form, appointments ->
            ScheduleUiState(
                form = form,
                appointments = appointments
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScheduleUiState()
        )

    fun setType(type: WorkoutType) {
        formFlow.update { it.copy(type = type) }
    }

    fun setScheduledTime(millis: Long) {
        formFlow.update { it.copy(scheduledTimeMillis = millis) }
    }

    fun setNote(note: String) {
        formFlow.update { it.copy(note = note) }
    }

    fun saveAppointment() {
        val form = formFlow.value
        viewModelScope.launch {
            repository.addAppointment(
                Appointment(
                    workoutType = form.type,
                    scheduledTimeMillis = form.scheduledTimeMillis,
                    note = form.note
                )
            )
            formFlow.update {
                it.copy(
                    note = "",
                    message = "Schedule saved."
                )
            }
        }
    }

    fun clearMessage() {
        formFlow.update { it.copy(message = null) }
    }

    fun formState(): StateFlow<ScheduleFormState> = formFlow.asStateFlow()
}