package au.edu.jcu.fittrackplus.data.repository

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
import au.edu.jcu.fittrackplus.domain.model.AppPreferences
import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitTrackRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val recordDao: RecordDao,
    private val appointmentDao: AppointmentDao,
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutTypeSettingDao: WorkoutTypeSettingDao,
    private val appPreferenceDao: AppPreferenceDao
) : FitTrackRepository {

    // ---------- User ----------
    override fun observeUserProfile(): Flow<UserProfile?> =
        userDao.observeUser().map { it?.toDomain() }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userDao.upsert(profile.toEntity())
    }

    // ---------- Records (durationSeconds) ----------
    override fun observeLatestRecord(): Flow<WorkoutRecord?> =
        recordDao.observeLatestRecord().map { it?.toDomain() }

    override suspend fun deleteRecordById(id: Long) {
        recordDao.deleteById(id)
    }

    override fun observeAllRecords(): Flow<List<WorkoutRecord>> =
        recordDao.observeAllRecords().map { list -> list.map { it.toDomain() } }

    override suspend fun addRecord(record: WorkoutRecord) {
        recordDao.insertRecord(record.toEntity())
    }

    // ---------- Appointments ----------
    override fun observeAppointments(): Flow<List<Appointment>> =
        appointmentDao.observeAppointments().map { list -> list.map { it.toDomain() } }

    override suspend fun addAppointment(appointment: Appointment) {
        appointmentDao.insertAppointment(appointment.toEntity())
    }

    // ---------- Workout Plans ----------
    override fun observeAllPlans(): Flow<List<WorkoutPlan>> =
        workoutPlanDao.observeAllPlans().map { list -> list.map { it.toDomain() } }

    override fun observePlanById(id: Long): Flow<WorkoutPlan?> =
        workoutPlanDao.observePlanById(id).map { it?.toDomain() }

    override suspend fun addPlan(plan: WorkoutPlan): Long =
        workoutPlanDao.insert(plan.toEntity())

    override suspend fun updatePlan(plan: WorkoutPlan) {
        workoutPlanDao.update(plan.toEntity())
    }

    override suspend fun deletePlanById(id: Long) {
        workoutPlanDao.deleteById(id)
    }

    // ---------- Workout Type Settings ----------
    override fun observeAllWorkoutTypeSettings() =
        workoutTypeSettingDao.observeAll().map { list ->
            val map = list.associate { WorkoutType.fromName(it.typeName) to it.enabled }
            WorkoutType.entries.associateWith { t -> map[t] ?: false }
        }

    override fun observeEnabledWorkoutTypes() =
        workoutTypeSettingDao.observeEnabled().map { list ->
            list.map { WorkoutType.fromName(it.typeName) }.sortedBy { it.name }
        }

    override suspend fun initWorkoutTypeSettingsIfEmpty() {
        val current = workoutTypeSettingDao.observeAll().map { it.size }.first()
        if (current > 0) return

        val defaultEnabled = setOf(
            WorkoutType.RUNNING, WorkoutType.WALKING, WorkoutType.CYCLING,
            WorkoutType.SWIMMING, WorkoutType.STRENGTH
        )

        workoutTypeSettingDao.upsertAll(
            WorkoutType.entries.map { t ->
                WorkoutTypeSettingEntity(typeName = t.name, enabled = defaultEnabled.contains(t))
            }
        )
    }

    override suspend fun setWorkoutTypeEnabled(type: WorkoutType, enabled: Boolean) {
        workoutTypeSettingDao.setEnabled(type.name, enabled)
    }

    // ---------- Preferences ----------
    override fun observePreferences() =
        appPreferenceDao.observeAll().map { list ->
            val map = list.associate { it.key to it.value }
            AppPreferences(
                language = map["LANG"] ?: "EN",
                theme = map["THEME"] ?: "LIGHT"
            )
        }

    override suspend fun setLanguage(lang: String) {
        appPreferenceDao.upsert(AppPreferenceEntity("LANG", lang))
    }

    override suspend fun setTheme(theme: String) {
        appPreferenceDao.upsert(AppPreferenceEntity("THEME", theme))
    }

    // ---------- Mappers ----------
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

    private fun WorkoutRecordEntity.toDomain(): WorkoutRecord {
        val parsedType = WorkoutType.fromName(workoutType)
        return WorkoutRecord(
            id = id,
            workoutType = parsedType,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            durationSeconds = durationSeconds, // ✅ seconds
            calories = calories,
            note = note
        )
    }

    private fun WorkoutRecord.toEntity() = WorkoutRecordEntity(
        id = id,
        workoutType = workoutType.name,
        startTimeMillis = startTimeMillis,
        endTimeMillis = endTimeMillis,
        durationSeconds = durationSeconds, // ✅ seconds
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