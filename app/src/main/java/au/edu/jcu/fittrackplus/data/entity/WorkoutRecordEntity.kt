package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a completed workout session recorded in history.
 *
 * Notes:
 * - [workoutType] should store a stable identifier (e.g., WorkoutType.name) rather than any localized label.
 * - Time values are stored as epoch milliseconds for interoperability and sorting.
 * - [durationSeconds] stores the actual duration with second-level precision.
 */
@Entity(tableName = "workout_records")
data class WorkoutRecordEntity(
    /** Auto-generated primary key for each record. */
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    /**
     * Workout type stored as a stable key (e.g., enum name like "RUNNING").
     * Avoid storing localized strings here.
     */
    val workoutType: String,

    /** Session start timestamp (epoch milliseconds). */
    val startTimeMillis: Long,

    /** Session end timestamp (epoch milliseconds). */
    val endTimeMillis: Long,

    /** Actual session duration in seconds (minimum/rounding rules are defined in domain logic). */
    val durationSeconds: Long,

    /** Calories burned for this session (estimated/calculated by domain logic). */
    val calories: Int,

    /** Optional user note attached to this record. */
    val note: String
)