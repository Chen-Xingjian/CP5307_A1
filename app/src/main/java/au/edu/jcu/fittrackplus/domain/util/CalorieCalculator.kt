package au.edu.jcu.fittrackplus.domain.util

import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import kotlin.math.roundToInt

object CalorieCalculator {

    /**
     * ✅ 秒级精确计算（全项目统一用它）
     */
    fun calculateSeconds(
        type: WorkoutType,
        durationSeconds: Long,
        profile: UserProfile?
    ): Int {
        val seconds = durationSeconds.coerceAtLeast(1L)
        val weightKg = (profile?.weightKg ?: 70.0).coerceAtLeast(30.0)
        val met = metOf(type)

        // kcal/min = MET * 3.5 * weight / 200
        val kcalPerMin = met * 3.5 * weightKg / 200.0

        // ✅ 秒级精确：按 seconds/60 比例缩放
        val total = kcalPerMin * (seconds / 60.0)

        // 建议最小 0（更真实）；你要最小 1 就改成 coerceAtLeast(1)
        return total.roundToInt().coerceAtLeast(0)
    }

    /**
     * 分钟版本（用于计划输入分钟时），但内部也走秒级精确计算，保证一致
     */
    fun calculate(
        type: WorkoutType,
        durationMinutes: Int,
        profile: UserProfile?
    ): Int {
        val minutes = durationMinutes.coerceAtLeast(1)
        return calculateSeconds(type, minutes.toLong() * 60L, profile)
    }

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