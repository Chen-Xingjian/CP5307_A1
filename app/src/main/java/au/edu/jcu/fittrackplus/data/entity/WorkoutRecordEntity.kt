package au.edu.jcu.fittrackplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_records")
data class WorkoutRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val exerciseType: String,
    val durationMinutes: Int,
    val calories: Int,
    val timestamp: Long,
    val note: String = ""
)