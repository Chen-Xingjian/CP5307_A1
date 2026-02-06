package au.edu.jcu.fittrackplus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import au.edu.jcu.fittrackplus.data.local.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.local.dao.RecordDao
import au.edu.jcu.fittrackplus.data.local.dao.UserDao
import au.edu.jcu.fittrackplus.data.local.entity.AppointmentEntity
import au.edu.jcu.fittrackplus.data.local.entity.UserEntity
import au.edu.jcu.fittrackplus.data.local.entity.WorkoutRecordEntity

@Database(
    entities = [UserEntity::class, WorkoutRecordEntity::class, AppointmentEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordDao(): RecordDao
    abstract fun appointmentDao(): AppointmentDao
}