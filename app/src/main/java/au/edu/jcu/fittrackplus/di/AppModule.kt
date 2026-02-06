package au.edu.jcu.fittrackplus.di

import android.content.Context
import androidx.room.Room
import au.edu.jcu.fittrackplus.data.local.AppDatabase
import au.edu.jcu.fittrackplus.data.local.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.local.dao.RecordDao
import au.edu.jcu.fittrackplus.data.local.dao.UserDao
import au.edu.jcu.fittrackplus.data.repository.FitTrackRepositoryImpl
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // 第1周可用 inMemory，后续改持久化
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideRecordDao(db: AppDatabase): RecordDao = db.recordDao()
    @Provides fun provideAppointmentDao(db: AppDatabase): AppointmentDao = db.appointmentDao()

    @Provides
    @Singleton
    fun provideRepository(
        userDao: UserDao,
        recordDao: RecordDao,
        appointmentDao: AppointmentDao
    ): FitTrackRepository = FitTrackRepositoryImpl(userDao, recordDao, appointmentDao)
}