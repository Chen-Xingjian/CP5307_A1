package au.edu.jcu.fittrackplus.domain.model

/**
 * Domain model representing a scheduled workout appointment.
 *
 * @property id Unique identifier of the appointment.
 * @property workoutType Workout type associated with this appointment.
 * @property scheduledTimeMillis Scheduled start time in epoch milliseconds.
 * @property note Optional user note for the appointment.
 */
data class Appointment(
    val id: Long = 0L,
    val workoutType: WorkoutType,
    val scheduledTimeMillis: Long,
    val note: String = ""
)