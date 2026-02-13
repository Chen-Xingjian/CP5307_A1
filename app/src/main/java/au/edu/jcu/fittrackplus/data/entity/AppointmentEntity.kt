package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workoutType: String,
    val scheduledTimeMillis: Long,
    val note: String
)