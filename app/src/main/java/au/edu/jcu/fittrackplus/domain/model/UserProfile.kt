package au.edu.jcu.fittrackplus.domain.model

data class UserProfile(
    val name: String,
    val gender: String,
    val age: Int,
    val heightCm: Double,
    val weightKg: Double,
    val preferredExercise: String
)