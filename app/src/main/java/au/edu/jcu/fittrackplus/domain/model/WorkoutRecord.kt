package au.edu.jcu.fittrackplus.domain.model

data class WorkoutRecord(
    val id: Long = 0L,
    val exerciseType: String,
    val durationMinutes: Int,
    val calories: Int,
    val timestamp: Long,
    val note: String = ""
)