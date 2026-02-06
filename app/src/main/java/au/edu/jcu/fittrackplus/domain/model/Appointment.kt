package au.edu.jcu.fittrackplus.domain.model

data class Appointment(
    val id: Long = 0L,
    val exerciseType: String,
    val scheduledTime: Long,
    val plannedDurationMinutes: Int,
    val note: String = ""
)