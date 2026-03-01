package au.edu.jcu.fittrackplus.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import au.edu.jcu.fittrackplus.data.dao.AppPreferenceDao
import au.edu.jcu.fittrackplus.data.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.dao.RecordDao
import au.edu.jcu.fittrackplus.data.dao.UserDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutPlanDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutTypeSettingDao
import au.edu.jcu.fittrackplus.data.entity.AppPreferenceEntity
import au.edu.jcu.fittrackplus.data.entity.AppointmentEntity
import au.edu.jcu.fittrackplus.data.entity.UserEntity
import au.edu.jcu.fittrackplus.data.entity.WorkoutPlanEntity
import au.edu.jcu.fittrackplus.data.entity.WorkoutRecordEntity
import au.edu.jcu.fittrackplus.data.entity.WorkoutTypeSettingEntity

/**
 * Main Room database for the application.
 *
 * Notes:
 * - When you change any entity schema (add/remove/rename columns, tables, indices),
 *   you must bump [version] and provide a migration strategy (or reset the DB).
 * - `exportSchema = false` disables schema export; enable it if you want migration history.
 */
@Database(
    entities = [
        UserEntity::class,
        WorkoutRecordEntity::class,
        AppointmentEntity::class,
        WorkoutPlanEntity::class,
        WorkoutTypeSettingEntity::class,
        AppPreferenceEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO for user profile data. */
    abstract fun userDao(): UserDao

    /** DAO for workout history records. */
    abstract fun recordDao(): RecordDao

    /** DAO for appointment/schedule items. */
    abstract fun appointmentDao(): AppointmentDao

    /** DAO for saved workout plans. */
    abstract fun workoutPlanDao(): WorkoutPlanDao

    /** DAO for workout type enable/disable settings. */
    abstract fun workoutTypeSettingDao(): WorkoutTypeSettingDao

    /** DAO for app preferences such as language/theme. */
    abstract fun appPreferenceDao(): AppPreferenceDao
}