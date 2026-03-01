package au.edu.jcu.fittrackplus.di

import android.content.Context
import androidx.room.Room
import au.edu.jcu.fittrackplus.data.dao.AppPreferenceDao
import au.edu.jcu.fittrackplus.data.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.dao.RecordDao
import au.edu.jcu.fittrackplus.data.dao.UserDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutPlanDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutTypeSettingDao
import au.edu.jcu.fittrackplus.data.database.AppDatabase
import au.edu.jcu.fittrackplus.data.repository.FitTrackRepositoryImpl
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds the repository implementation to the repository interface.
 *
 * This module uses @Binds to avoid creating the instance manually; Hilt will provide
 * [FitTrackRepositoryImpl] using its @Inject constructor.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds [FitTrackRepositoryImpl] as the concrete implementation for [FitTrackRepository].
     */
    @Binds
    @Singleton
    abstract fun bindRepository(impl: FitTrackRepositoryImpl): FitTrackRepository
}

/**
 * Hilt module that provides Room database and DAO instances.
 *
 * Notes:
 * - [fallbackToDestructiveMigration] will wipe and rebuild the database on schema changes
 *   when no proper migration is provided. This is convenient during development but
 *   not recommended for production apps where data loss is unacceptable.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton [AppDatabase] instance backed by Room.
     */
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "fittrackplus.db")
            .fallbackToDestructiveMigration()
            .build()

    /**
     * Provides [UserDao] from the Room database instance.
     */
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    /**
     * Provides [RecordDao] from the Room database instance.
     */
    @Provides
    fun provideRecordDao(db: AppDatabase): RecordDao = db.recordDao()

    /**
     * Provides [AppointmentDao] from the Room database instance.
     */
    @Provides
    fun provideAppointmentDao(db: AppDatabase): AppointmentDao = db.appointmentDao()

    /**
     * Provides [WorkoutPlanDao] from the Room database instance.
     */
    @Provides
    fun provideWorkoutPlanDao(db: AppDatabase): WorkoutPlanDao = db.workoutPlanDao()

    /**
     * Provides [WorkoutTypeSettingDao] from the Room database instance.
     */
    @Provides
    fun provideWorkoutTypeSettingDao(db: AppDatabase): WorkoutTypeSettingDao =
        db.workoutTypeSettingDao()

    /**
     * Provides [AppPreferenceDao] from the Room database instance.
     */
    @Provides
    fun provideAppPreferenceDao(db: AppDatabase): AppPreferenceDao = db.appPreferenceDao()
}