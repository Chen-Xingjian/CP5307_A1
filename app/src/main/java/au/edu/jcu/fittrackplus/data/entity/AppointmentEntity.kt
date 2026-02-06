package au.edu.jcu.fittrackplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val exerciseType: String,
    val scheduledTime: Long,
    val plannedDurationMinutes: Int,
    val note: String = ""
)