package au.edu.jcu.fittrackplus.domain.model

data class Appointment(
    val id: Long = 0L,
    val workoutType: WorkoutType,
    val scheduledTimeMillis: Long,
    val note: String = ""
)