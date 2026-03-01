package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing whether a specific [WorkoutType] is enabled (applied) in the app.
 *
 * Notes:
 * - [typeName] should store a stable identifier (e.g., WorkoutType.name like "RUNNING").
 * - UI should localize/display labels separately; do not store localized text in the database.
 */
@Entity(tableName = "workout_type_settings")
data class WorkoutTypeSettingEntity(
    /** Stable workout type key (e.g., WorkoutType.name). */
    @PrimaryKey val typeName: String, // WorkoutType.name

    /** Whether this workout type is enabled (available) for selection in the UI. */
    val enabled: Boolean
)