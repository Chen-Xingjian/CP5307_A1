package au.edu.jcu.fittrackplus.domain.util

import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import kotlin.math.roundToInt

/**
 * Utility for estimating calorie burn based on MET values.
 *
 * Notes:
 * - This implementation uses a simplified MET-based formula for an estimate, not a medical-grade value.
 * - The app standardizes on **second-level precision** via [calculateSeconds] to keep results consistent.
 */
object CalorieCalculator {

    /**
     * Calculates estimated calories using second-level precision.
     *
     * Formula:
     * - kcal/min = MET * 3.5 * weight(kg) / 200
     * - total kcal = kcal/min * (seconds / 60)
     *
     * Safeguards:
     * - durationSeconds is clamped to at least 1 second
     * - weightKg uses a reasonable lower bound to avoid extreme values
     *
     * @param type Workout type used to pick a MET value.
     * @param durationSeconds Workout duration in seconds.
     * @param profile Optional user profile (used for weight).
     * @return Estimated calories as an Int (rounded), minimum is 0.
     */
    fun calculateSeconds(
        type: WorkoutType,
        durationSeconds: Long,
        profile: UserProfile?
    ): Int {
        val seconds = durationSeconds.coerceAtLeast(1L)
        val weightKg = (profile?.weightKg ?: 70.0).coerceAtLeast(30.0)
        val met = metOf(type)

        // kcal/min = MET * 3.5 * weight(kg) / 200
        val kcalPerMin = met * 3.5 * weightKg / 200.0

        // Scale linearly by time in minutes (seconds / 60.0).
        val total = kcalPerMin * (seconds / 60.0)

        // Return a realistic minimum (0). If you prefer 1, change to coerceAtLeast(1).
        return total.roundToInt().coerceAtLeast(0)
    }

    /**
     * Convenience overload for minute-based inputs (e.g., plan creation).
     *
     * This delegates to [calculateSeconds] to ensure consistent logic across the app.
     *
     * @param type Workout type used to pick a MET value.
     * @param durationMinutes Workout duration in minutes.
     * @param profile Optional user profile (used for weight).
     * @return Estimated calories as an Int (rounded), minimum is 0.
     */
    fun calculate(
        type: WorkoutType,
        durationMinutes: Int,
        profile: UserProfile?
    ): Int {
        val minutes = durationMinutes.coerceAtLeast(1)
        return calculateSeconds(type, minutes.toLong() * 60L, profile)
    }

    /**
     * Returns a MET value for a given [WorkoutType].
     *
     * These are approximate "moderate intensity" defaults.
     */
    private fun metOf(type: WorkoutType): Double {
        return when (type) {
            WorkoutType.RUNNING -> 9.8
            WorkoutType.WALKING -> 3.5
            WorkoutType.CYCLING -> 7.5
            WorkoutType.SWIMMING -> 8.0
            WorkoutType.STRENGTH -> 6.0
            WorkoutType.YOGA -> 2.8
            WorkoutType.HIIT -> 10.0
            WorkoutType.PILATES -> 3.0
            WorkoutType.ROWING -> 7.0
            WorkoutType.HIKING -> 6.0
            WorkoutType.ELLIPTICAL -> 5.0
            WorkoutType.STAIR_CLIMBING -> 8.8
            WorkoutType.JUMP_ROPE -> 11.0
            WorkoutType.BOXING -> 8.0
            WorkoutType.BADMINTON -> 5.5
            WorkoutType.BASKETBALL -> 6.5
            WorkoutType.FOOTBALL -> 7.0
            WorkoutType.TENNIS -> 7.3
            WorkoutType.TABLE_TENNIS -> 4.0
            WorkoutType.VOLLEYBALL -> 3.0
            WorkoutType.DANCE -> 5.0
            WorkoutType.SKIPPING -> 8.0
            WorkoutType.SKATING -> 7.0
            WorkoutType.CLIMBING -> 8.0
            WorkoutType.MARTIAL_ARTS -> 10.3
        }
    }
}