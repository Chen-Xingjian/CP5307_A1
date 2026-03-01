package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a scheduled workout appointment.
 *
 * Table: appointments
 * - [workoutType] stores the stable enum name (e.g., "RUNNING") rather than a localized label.
 * - [scheduledTimeMillis] is the scheduled start time in epoch milliseconds.
 */
@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    /** Workout type identifier (prefer enum name such as "RUNNING"). */
    val workoutType: String,

    /** Scheduled time in epoch milliseconds. */
    val scheduledTimeMillis: Long,

    /** Optional note for the appointment. */
    val note: String
)