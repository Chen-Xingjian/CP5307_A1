package au.edu.jcu.fittrackplus.domain.model

/**
 * Domain model representing a user's profile used by the app.
 *
 * Notes:
 * - This app stores a single user row using a fixed primary key (default id = 1).
 * - gender is stored as a stable key (e.g., "male", "female", "other") and can be mapped to i18n strings in UI.
 * - preferredExercise is stored as a stable key (WorkoutType.name), e.g., "RUNNING".
 *
 * @property id Fixed user id (single-profile design).
 * @property name Display name.
 * @property gender Stable gender key for persistence and UI mapping.
 * @property age User age (years).
 * @property heightCm Height in centimeters.
 * @property weightKg Weight in kilograms.
 * @property preferredExercise Default exercise type key (WorkoutType.name).
 */
data class UserProfile(
    val id: Int = 1,
    val name: String = "",
    val gender: String = "",
    val age: Int = 25,
    val heightCm: Double = 170.0,
    val weightKg: Double = 70.0,
    val preferredExercise: String = "RUNNING"
)