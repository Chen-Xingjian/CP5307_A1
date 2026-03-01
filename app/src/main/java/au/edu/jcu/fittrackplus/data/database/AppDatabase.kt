package au.edu.jcu.fittrackplus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import au.edu.jcu.fittrackplus.data.dao.*
import au.edu.jcu.fittrackplus.data.entity.*

@Database(
    entities = [
        UserEntity::class,
        WorkoutRecordEntity::class,
        AppointmentEntity::class,
        WorkoutPlanEntity::class,

        WorkoutTypeSettingEntity::class,
        AppPreferenceEntity::class
    ],
    version = 4, // 记得 +1
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordDao(): RecordDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun workoutPlanDao(): WorkoutPlanDao

    abstract fun workoutTypeSettingDao(): WorkoutTypeSettingDao
    abstract fun appPreferenceDao(): AppPreferenceDao
}