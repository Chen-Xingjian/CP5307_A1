package au.edu.jcu.fittrackplus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import au.edu.jcu.fittrackplus.data.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.dao.RecordDao
import au.edu.jcu.fittrackplus.data.dao.UserDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutPlanDao
import au.edu.jcu.fittrackplus.data.entity.AppointmentEntity
import au.edu.jcu.fittrackplus.data.entity.UserEntity
import au.edu.jcu.fittrackplus.data.entity.WorkoutPlanEntity
import au.edu.jcu.fittrackplus.data.entity.WorkoutRecordEntity

@Database(
    entities = [
        UserEntity::class,
        WorkoutRecordEntity::class,
        AppointmentEntity::class,
        WorkoutPlanEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordDao(): RecordDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
}