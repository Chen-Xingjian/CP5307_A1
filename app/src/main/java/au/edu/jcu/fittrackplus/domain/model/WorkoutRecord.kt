package au.edu.jcu.fittrackplus.domain.model

/**
 * Domain model representing a completed workout session saved to history.
 *
 * Notes:
 * - startTimeMillis/endTimeMillis are stored as epoch milliseconds.
 * - durationSeconds is the persisted duration for the workout (precision to seconds).
 * - calories is the estimated energy expenditure for the session (kcal).
 *
 * @property id Database id (auto-generated).
 * @property workoutType Workout type for the session.
 * @property startTimeMillis Start timestamp (epoch millis).
 * @property endTimeMillis End timestamp (epoch millis).
 * @property durationSeconds Duration in seconds (>= 1 when saved).
 * @property calories Estimated calories burned (kcal).
 * @property note Optional user note for the record.
 */
data class WorkoutRecord(
    val id: Long = 0L,
    val workoutType: WorkoutType,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val calories: Int,
    val note: String = ""
)