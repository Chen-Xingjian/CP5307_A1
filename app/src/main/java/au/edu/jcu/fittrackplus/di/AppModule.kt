package au.edu.jcu.fittrackplus.di

import android.content.Context
import androidx.room.Room
import au.edu.jcu.fittrackplus.data.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.dao.RecordDao
import au.edu.jcu.fittrackplus.data.dao.UserDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutPlanDao
import au.edu.jcu.fittrackplus.data.database.AppDatabase
import au.edu.jcu.fittrackplus.data.repository.FitTrackRepositoryImpl
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import au.edu.jcu.fittrackplus.data.dao.WorkoutTypeSettingDao
import au.edu.jcu.fittrackplus.data.dao.AppPreferenceDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: FitTrackRepositoryImpl): FitTrackRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "fittrackplus.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideRecordDao(db: AppDatabase): RecordDao = db.recordDao()

    @Provides
    fun provideAppointmentDao(db: AppDatabase): AppointmentDao = db.appointmentDao()

    @Provides
    fun provideWorkoutPlanDao(db: AppDatabase): WorkoutPlanDao = db.workoutPlanDao()

    @Provides
    fun provideWorkoutTypeSettingDao(db: AppDatabase): WorkoutTypeSettingDao = db.workoutTypeSettingDao()

    @Provides
    fun provideAppPreferenceDao(db: AppDatabase): AppPreferenceDao = db.appPreferenceDao()

}