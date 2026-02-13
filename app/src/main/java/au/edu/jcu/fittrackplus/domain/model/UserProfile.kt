package au.edu.jcu.fittrackplus.domain.model

data class UserProfile(
    val id: Int = 1,
    val name: String = "",
    val gender: String = "",
    val age: Int = 25,
    val heightCm: Double = 170.0,
    val weightKg: Double = 70.0,
    val preferredExercise: String = "RUNNING"
)