package au.edu.jcu.fittrackplus.domain.model

data class WorkoutRecord(
    val id: Long = 0L,
    val workoutType: WorkoutType,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val calories: Int,
    val note: String = ""
)