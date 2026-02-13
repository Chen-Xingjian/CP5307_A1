package au.edu.jcu.fittrackplus.domain.util

import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import kotlin.math.round

object CalorieCalculator {

    // 简化 MET 估算
    private fun met(type: WorkoutType): Double = when (type) {
        WorkoutType.RUNNING -> 9.8
        WorkoutType.CYCLING -> 7.5
        WorkoutType.SWIMMING -> 8.0
        WorkoutType.WALKING -> 3.8
        WorkoutType.STRENGTH -> 6.0
        WorkoutType.YOGA -> 2.8
    }

    fun calculate(type: WorkoutType, durationMinutes: Int, profile: UserProfile?): Double {
        val weight = profile?.weightKg ?: 70.0
        val hours = durationMinutes / 60.0
        val kcal = met(type) * weight * hours
        return round(kcal * 10) / 10.0
    }
}