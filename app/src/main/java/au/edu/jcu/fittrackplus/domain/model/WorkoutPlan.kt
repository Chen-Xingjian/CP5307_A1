package au.edu.jcu.fittrackplus.domain.model

data class WorkoutPlan(
    val id: Long = 0L,
    val name: String,
    val category: String,
    val durationMinutes: Int,
    val estimatedCalories: Int,
    val note: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)