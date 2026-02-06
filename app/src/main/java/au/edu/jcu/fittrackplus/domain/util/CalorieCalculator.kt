package au.edu.jcu.fittrackplus.domain.util

object CalorieCalculator {
    fun metFor(type: String): Double = when (type.lowercase()) {
        "running" -> 9.8
        "walking" -> 3.8
        "cycling" -> 7.5
        "swimming" -> 8.0
        "strength" -> 6.0
        else -> 5.0
    }

    fun estimate(weightKg: Double, durationMin: Int, met: Double): Int {
        val hours = durationMin / 60.0
        return (met * weightKg * hours).toInt().coerceAtLeast(0)
    }
}