package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plan")
data class WorkoutPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val category: String,
    val durationMinutes: Int,
    val estimatedCalories: Int,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long
)