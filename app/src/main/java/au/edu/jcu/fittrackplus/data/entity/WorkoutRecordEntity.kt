package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_records")
data class WorkoutRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workoutType: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Int,
    val calories: Double,
    val note: String
)