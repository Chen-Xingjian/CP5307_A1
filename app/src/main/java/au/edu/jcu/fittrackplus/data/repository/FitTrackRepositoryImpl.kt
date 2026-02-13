package au.edu.jcu.fittrackplus.data.repository

import au.edu.jcu.fittrackplus.data.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.dao.RecordDao
import au.edu.jcu.fittrackplus.data.dao.UserDao
import au.edu.jcu.fittrackplus.data.entity.AppointmentEntity
import au.edu.jcu.fittrackplus.data.entity.UserEntity
import au.edu.jcu.fittrackplus.data.entity.WorkoutRecordEntity
import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitTrackRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val recordDao: RecordDao,
    private val appointmentDao: AppointmentDao
) : FitTrackRepository {

    override fun observeUserProfile(): Flow<UserProfile?> =
        userDao.observeUser().map { it?.toDomain() }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userDao.upsert(profile.toEntity())
    }

    override fun observeLatestRecord(): Flow<WorkoutRecord?> =
        recordDao.observeLatestRecord().map { it?.toDomain() }

    override fun observeAllRecords(): Flow<List<WorkoutRecord>> =
        recordDao.observeAllRecords().map { list -> list.map { it.toDomain() } }

    override suspend fun addRecord(record: WorkoutRecord) {
        recordDao.insertRecord(record.toEntity())
    }

    override fun observeAppointments(): Flow<List<Appointment>> =
        appointmentDao.observeAppointments().map { list -> list.map { it.toDomain() } }

    override suspend fun addAppointment(appointment: Appointment) {
        appointmentDao.insertAppointment(appointment.toEntity())
    }

    private fun UserEntity.toDomain() = UserProfile(
        id = id,
        name = name,
        gender = gender,
        age = age,
        heightCm = heightCm,
        weightKg = weightKg,
        preferredExercise = preferredExercise
    )

    private fun UserProfile.toEntity() = UserEntity(
        id = id,
        name = name,
        gender = gender,
        age = age,
        heightCm = heightCm,
        weightKg = weightKg,
        preferredExercise = preferredExercise
    )

    private fun WorkoutRecordEntity.toDomain() = WorkoutRecord(
        id = id,
        workoutType = WorkoutType.fromName(workoutType),
        startTimeMillis = startTimeMillis,
        endTimeMillis = endTimeMillis,
        durationMinutes = durationMinutes,
        calories = calories,
        note = note
    )

    private fun WorkoutRecord.toEntity() = WorkoutRecordEntity(
        id = id,
        workoutType = workoutType.name,
        startTimeMillis = startTimeMillis,
        endTimeMillis = endTimeMillis,
        durationMinutes = durationMinutes,
        calories = calories,
        note = note
    )

    private fun AppointmentEntity.toDomain() = Appointment(
        id = id,
        workoutType = WorkoutType.fromName(workoutType),
        scheduledTimeMillis = scheduledTimeMillis,
        note = note
    )

    private fun Appointment.toEntity() = AppointmentEntity(
        id = id,
        workoutType = workoutType.name,
        scheduledTimeMillis = scheduledTimeMillis,
        note = note
    )
}