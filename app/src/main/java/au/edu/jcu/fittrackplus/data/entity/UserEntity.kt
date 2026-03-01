package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing the user's profile.
 *
 * Notes:
 * - This app stores a single user profile row, identified by a fixed [id] (default = 1).
 * - Domain-layer mapping should handle display/i18n concerns; this entity should only store raw values.
 */
@Entity(tableName = "user_profile")
data class UserEntity(
    /** Fixed primary key for the single stored profile (default: 1). */
    @PrimaryKey val id: Int = 1,

    /** User display name. */
    val name: String,

    /**
     * User gender value stored as a stable key (e.g., "male", "female", "other").
     * Keep this value language-agnostic for i18n mapping.
     */
    val gender: String,

    /** User age in years. */
    val age: Int,

    /** User height in centimeters. */
    val heightCm: Double,

    /** User weight in kilograms. */
    val weightKg: Double,

    /**
     * Preferred exercise stored as a stable identifier (e.g., enum name or key),
     * not a localized display label.
     */
    val preferredExercise: String
)