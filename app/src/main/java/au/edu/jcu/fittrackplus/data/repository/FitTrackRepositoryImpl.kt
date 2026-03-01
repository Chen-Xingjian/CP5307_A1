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

/**
 * Default implementation of [FitTrackRepository].
 *
 * Responsibilities:
 * - Bridges Room DAOs and domain models.
 * - Exposes reactive streams (Flow) for UI/view-model layers.
 * - Keeps persistence details (entities / table schemas) out of the domain layer.
 *
 * Notes:
 * - This repository intentionally stores stable identifiers in DB (e.g., WorkoutType.name),
 *   and UI performs localization based on [AppStrings].
 * - Preferences are stored as key-value rows in [AppPreferenceEntity] ("LANG", "THEME").
 */
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

    /**
     * Observes the single user profile row (id=1) as a domain model.
     */
    override fun observeUserProfile(): Flow<UserProfile?> =
        userDao.observeUser().map { it?.toDomain() }

    /**
     * Inserts or replaces the single user profile row.
     */
    override suspend fun saveUserProfile(profile: UserProfile) {
        userDao.upsert(profile.toEntity())
    }

    // ---------- Records (durationSeconds) ----------

    /**
     * Observes the most recent workout record (by start time).
     */
    override fun observeLatestRecord(): Flow<WorkoutRecord?> =
        recordDao.observeLatestRecord().map { it?.toDomain() }

    /**
     * Deletes a workout record by its primary key.
     */
    override suspend fun deleteRecordById(id: Long) {
        recordDao.deleteById(id)
    }

    /**
     * Observes all workout records sorted by start time descending.
     */
    override fun observeAllRecords(): Flow<List<WorkoutRecord>> =
        recordDao.observeAllRecords().map { list -> list.map { it.toDomain() } }

    /**
     * Persists a workout record.
     */
    override suspend fun addRecord(record: WorkoutRecord) {
        recordDao.insertRecord(record.toEntity())
    }

    // ---------- Appointments ----------

    /**
     * Observes all scheduled appointments.
     */
    override fun observeAppointments(): Flow<List<Appointment>> =
        appointmentDao.observeAppointments().map { list -> list.map { it.toDomain() } }

    /**
     * Persists an appointment.
     */
    override suspend fun addAppointment(appointment: Appointment) {
        appointmentDao.insertAppointment(appointment.toEntity())
    }

    // ---------- Workout Plans ----------

    /**
     * Observes all workout plans sorted by last update time descending.
     */
    override fun observeAllPlans(): Flow<List<WorkoutPlan>> =
        workoutPlanDao.observeAllPlans().map { list -> list.map { it.toDomain() } }

    /**
     * Observes a single plan by id.
     */
    override fun observePlanById(id: Long): Flow<WorkoutPlan?> =
        workoutPlanDao.observePlanById(id).map { it?.toDomain() }

    /**
     * Inserts a plan and returns the generated id.
     */
    override suspend fun addPlan(plan: WorkoutPlan): Long =
        workoutPlanDao.insert(plan.toEntity())

    /**
     * Updates a plan.
     */
    override suspend fun updatePlan(plan: WorkoutPlan) {
        workoutPlanDao.update(plan.toEntity())
    }

    /**
     * Deletes a plan by id.
     */
    override suspend fun deletePlanById(id: Long) {
        workoutPlanDao.deleteById(id)
    }

    // ---------- Workout Type Settings ----------

    /**
     * Observes a full map of workout types -> enabled flags.
     * Ensures all [WorkoutType] entries exist in the returned map (defaulting to false).
     */
    override fun observeAllWorkoutTypeSettings() =
        workoutTypeSettingDao.observeAll().map { list ->
            val map = list.associate { WorkoutType.fromName(it.typeName) to it.enabled }
            WorkoutType.entries.associateWith { t -> map[t] ?: false }
        }

    /**
     * Observes only enabled workout types (sorted by enum name).
     */
    override fun observeEnabledWorkoutTypes() =
        workoutTypeSettingDao.observeEnabled().map { list ->
            list.map { WorkoutType.fromName(it.typeName) }.sortedBy { it.name }
        }

    /**
     * Initializes workout type settings if the table is empty.
     * A subset of types is enabled by default.
     */
    override suspend fun initWorkoutTypeSettingsIfEmpty() {
        // Read once: if there are any rows, skip initialization.
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

    /**
     * Updates a single workout type enabled flag.
     */
    override suspend fun setWorkoutTypeEnabled(type: WorkoutType, enabled: Boolean) {
        workoutTypeSettingDao.setEnabled(type.name, enabled)
    }

    // ---------- Preferences ----------

    /**
     * Observes app preferences stored as key-value rows.
     *
     * Keys:
     * - "LANG"  -> "EN" / "ZH"
     * - "THEME" -> "LIGHT" / "DARK"
     */
    override fun observePreferences() =
        appPreferenceDao.observeAll().map { list ->
            val map = list.associate { it.key to it.value }
            AppPreferences(
                language = map["LANG"] ?: "EN",
                theme = map["THEME"] ?: "LIGHT"
            )
        }

    /**
     * Updates preferred language.
     *
     * @param lang Usually "EN" or "ZH".
     */
    override suspend fun setLanguage(lang: String) {
        appPreferenceDao.upsert(AppPreferenceEntity("LANG", lang))
    }

    /**
     * Updates preferred theme.
     *
     * @param theme Usually "LIGHT" or "DARK".
     */
    override suspend fun setTheme(theme: String) {
        appPreferenceDao.upsert(AppPreferenceEntity("THEME", theme))
    }

    // ---------- Mappers ----------
    // Keep mapping logic private to avoid leaking entity details.

    /**
     * Maps [UserEntity] -> [UserProfile].
     */
    private fun UserEntity.toDomain() = UserProfile(
        id = id,
        name = name,
        gender = gender,
        age = age,
        heightCm = heightCm,
        weightKg = weightKg,
        preferredExercise = preferredExercise
    )

    /**
     * Maps [UserProfile] -> [UserEntity].
     */
    private fun UserProfile.toEntity() = UserEntity(
        id = id,
        name = name,
        gender = gender,
        age = age,
        heightCm = heightCm,
        weightKg = weightKg,
        preferredExercise = preferredExercise
    )

    /**
     * Maps [WorkoutRecordEntity] -> [WorkoutRecord].
     *
     * Notes:
     * - workoutType is stored as a stable enum name.
     * - duration is stored in seconds to preserve short sessions.
     */
    private fun WorkoutRecordEntity.toDomain(): WorkoutRecord {
        val parsedType = WorkoutType.fromName(workoutType)
        return WorkoutRecord(
            id = id,
            workoutType = parsedType,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            durationSeconds = durationSeconds,
            calories = calories,
            note = note
        )
    }

    /**
     * Maps [WorkoutRecord] -> [WorkoutRecordEntity].
     */
    private fun WorkoutRecord.toEntity() = WorkoutRecordEntity(
        id = id,
        workoutType = workoutType.name,
        startTimeMillis = startTimeMillis,
        endTimeMillis = endTimeMillis,
        durationSeconds = durationSeconds,
        calories = calories,
        note = note
    )

    /**
     * Maps [AppointmentEntity] -> [Appointment].
     */
    private fun AppointmentEntity.toDomain() = Appointment(
        id = id,
        workoutType = WorkoutType.fromName(workoutType),
        scheduledTimeMillis = scheduledTimeMillis,
        note = note
    )

    /**
     * Maps [Appointment] -> [AppointmentEntity].
     */
    private fun Appointment.toEntity() = AppointmentEntity(
        id = id,
        workoutType = workoutType.name,
        scheduledTimeMillis = scheduledTimeMillis,
        note = note
    )

    /**
     * Maps [WorkoutPlanEntity] -> [WorkoutPlan].
     *
     * Notes:
     * - category stores WorkoutType.name (stable key), not localized text.
     */
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

    /**
     * Maps [WorkoutPlan] -> [WorkoutPlanEntity].
     */
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