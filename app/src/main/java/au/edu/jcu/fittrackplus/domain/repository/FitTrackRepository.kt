package au.edu.jcu.fittrackplus.domain.repository

import au.edu.jcu.fittrackplus.domain.model.AppPreferences
import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

interface FitTrackRepository {
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)

    suspend fun deleteRecordById(id: Long)

    fun observeLatestRecord(): Flow<WorkoutRecord?>
    fun observeAllRecords(): Flow<List<WorkoutRecord>>
    suspend fun addRecord(record: WorkoutRecord)

    fun observeAppointments(): Flow<List<Appointment>>
    suspend fun addAppointment(appointment: Appointment)

    // ===== Workout Plan =====
    fun observeAllPlans(): Flow<List<WorkoutPlan>>
    fun observePlanById(id: Long): Flow<WorkoutPlan?>
    suspend fun addPlan(plan: WorkoutPlan): Long
    suspend fun updatePlan(plan: WorkoutPlan)
    // domain/repository/FitTrackRepository.kt
    suspend fun deletePlanById(id: Long)

    // ----- Category settings -----
    fun observeAllWorkoutTypeSettings(): Flow<Map<WorkoutType, Boolean>>
    fun observeEnabledWorkoutTypes(): Flow<List<WorkoutType>>
    suspend fun initWorkoutTypeSettingsIfEmpty()
    suspend fun setWorkoutTypeEnabled(type: WorkoutType, enabled: Boolean)

    // ----- Preferences -----
    fun observePreferences(): Flow<AppPreferences>
    suspend fun setLanguage(lang: String) // "EN" / "ZH"
    suspend fun setTheme(theme: String)   // "LIGHT" / "DARK"
}