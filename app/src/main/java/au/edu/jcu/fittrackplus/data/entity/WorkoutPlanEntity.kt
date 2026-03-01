package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a saved workout plan/template.
 *
 * Notes:
 * - [category] should store a stable identifier (e.g., WorkoutType.name) rather than any localized label.
 * - [durationMinutes] is the planned duration for the workout session.
 * - [estimatedCalories] is an estimated burn for the planned duration (business rules live in domain/util).
 * - [createdAt] / [updatedAt] are epoch milliseconds used for sorting and auditing.
 */
@Entity(tableName = "workout_plan")
data class WorkoutPlanEntity(
    /** Auto-generated primary key for each plan. */
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    /** User-defined plan name. */
    val name: String,

    /**
     * Workout type/category stored as a stable key (e.g., enum name like "RUNNING").
     * Do not store localized strings here.
     */
    val category: String,

    /** Planned duration in minutes. */
    val durationMinutes: Int,

    /** Estimated calories burned for the planned duration. */
    val estimatedCalories: Int,

    /** Optional notes for the plan. */
    val note: String,

    /** Creation timestamp (epoch milliseconds). */
    val createdAt: Long,

    /** Last update timestamp (epoch milliseconds). */
    val updatedAt: Long
)