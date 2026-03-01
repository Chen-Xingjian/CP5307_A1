package au.edu.jcu.fittrackplus.domain.model

/**
 * Domain model representing a reusable workout plan created by the user.
 *
 * Notes:
 * - category is stored as a stable identifier (WorkoutType.name), e.g., "RUNNING".
 * - durationMinutes is the planned duration in minutes for the plan.
 * - estimatedCalories is a precomputed estimate for the plan (kcal), typically derived from duration + MET model.
 * - createdAt/updatedAt are stored as epoch milliseconds.
 *
 * @property id Database id (auto-generated).
 * @property name Plan display name.
 * @property category Workout type key (WorkoutType.name).
 * @property durationMinutes Planned duration in minutes.
 * @property estimatedCalories Estimated energy expenditure in kcal.
 * @property note Optional user note for the plan.
 * @property createdAt Creation timestamp (epoch millis).
 * @property updatedAt Last update timestamp (epoch millis).
 */
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