package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing simple key-value application preferences.
 *
 * Table: app_preferences
 * - [key] is the unique identifier for a preference (e.g., "LANG", "THEME").
 * - [value] stores the corresponding preference value (e.g., "EN", "DARK").
 */
@Entity(tableName = "app_preferences")
data class AppPreferenceEntity(
    /** Preference key (acts as the primary key). */
    @PrimaryKey val key: String,

    /** Preference value associated with [key]. */
    val value: String
)