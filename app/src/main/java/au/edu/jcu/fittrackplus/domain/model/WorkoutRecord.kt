package au.edu.jcu.fittrackplus.domain.model

data class WorkoutRecord(
    val id: Long = 0L,
    val workoutType: WorkoutType,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Int,
    val calories: Double,
    val note: String = ""
)