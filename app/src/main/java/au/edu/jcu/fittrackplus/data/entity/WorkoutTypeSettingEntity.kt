package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_type_settings")
data class WorkoutTypeSettingEntity(
    @PrimaryKey val typeName: String, // WorkoutType.name
    val enabled: Boolean
)