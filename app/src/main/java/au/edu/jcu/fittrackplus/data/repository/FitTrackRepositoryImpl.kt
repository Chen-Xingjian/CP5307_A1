package au.edu.jcu.fittrackplus.data.repository

import au.edu.jcu.fittrackplus.data.dao.AppointmentDao
import au.edu.jcu.fittrackplus.data.dao.RecordDao
import au.edu.jcu.fittrackplus.data.dao.UserDao
import au.edu.jcu.fittrackplus.data.dao.WorkoutPlanDao
import au.edu.jcu.fittrackplus.data.entity.WorkoutPlanEntity
import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitTrackRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val recordDao: RecordDao,
    private val appointmentDao: AppointmentDao,
    private val workoutPlanDao: WorkoutPlanDao
) : FitTrackRepository {

    override fun observeUserProfile(): Flow<UserProfile?> = TODO()
    override suspend fun saveUserProfile(profile: UserProfile) = TODO()

    override fun observeLatestRecord(): Flow<WorkoutRecord?> = TODO()
    override fun observeAllRecords(): Flow<List<WorkoutRecord>> = TODO()
    override suspend fun addRecord(record: WorkoutRecord) = TODO()

    override fun observeAppointments(): Flow<List<Appointment>> = TODO()
    override suspend fun addAppointment(appointment: Appointment) = TODO()

    // ===== Plan =====
    override fun observeAllPlans(): Flow<List<WorkoutPlan>> =
        workoutPlanDao.observeAllPlans().map { list -> list.map { it.toDomain() } }

    override fun observePlanById(id: Long): Flow<WorkoutPlan?> =
        workoutPlanDao.observePlanById(id).map { it?.toDomain() }

    override suspend fun addPlan(plan: WorkoutPlan): Long =
        workoutPlanDao.insert(plan.toEntity())

    override suspend fun updatePlan(plan: WorkoutPlan) {
        workoutPlanDao.update(plan.toEntity())
    }

    private fun WorkoutPlanEntity.toDomain() = WorkoutPlan(
        id = id,
        name = name,
        category = category,
        durationMinutes = durationMinutes,
        estimatedCalories = estimatedCalories,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun WorkoutPlan.toEntity() = WorkoutPlanEntity(
        id = id,
        name = name,
        category = category,
        durationMinutes = durationMinutes,
        estimatedCalories = estimatedCalories,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}