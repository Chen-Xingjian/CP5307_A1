package au.edu.jcu.fittrackplus.domain.repository

import au.edu.jcu.fittrackplus.domain.model.AppPreferences
import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
 * Domain-facing contract for all app data operations.
 *
 * This interface defines a clean API for the UI layer and ViewModels.
 * The data layer (e.g., Room + DAOs) should be hidden behind this abstraction.
 *
 * Conventions:
 * - All "observe*" methods return cold [Flow] streams that emit updates.
 * - "save/add/update/delete" methods are suspend functions intended to be called from coroutines.
 * - Persisted identifiers should use stable enum names (e.g., [WorkoutType.name]).
 */
interface FitTrackRepository {

    // ----- User Profile -----

    /**
     * Observes the single user profile record.
     *
     * @return A flow that emits the latest profile, or null if not created yet.
     */
    fun observeUserProfile(): Flow<UserProfile?>

    /**
     * Creates or updates the user profile.
     *
     * @param profile The profile to persist.
     */
    suspend fun saveUserProfile(profile: UserProfile)

    // ----- Workout Records -----

    /**
     * Deletes a workout record by its primary key.
     *
     * @param id Record ID to delete.
     */
    suspend fun deleteRecordById(id: Long)

    /**
     * Observes the most recent workout record (by start time).
     *
     * @return A flow emitting the latest record or null when there are no records.
     */
    fun observeLatestRecord(): Flow<WorkoutRecord?>

    /**
     * Observes all workout records ordered by start time descending.
     */
    fun observeAllRecords(): Flow<List<WorkoutRecord>>

    /**
     * Inserts a new workout record.
     *
     * Note: The underlying storage may use auto-generated IDs.
     */
    suspend fun addRecord(record: WorkoutRecord)

    // ----- Appointments (Optional / Future Use) -----

    /**
     * Observes scheduled appointments.
     */
    fun observeAppointments(): Flow<List<Appointment>>

    /**
     * Adds a scheduled appointment.
     */
    suspend fun addAppointment(appointment: Appointment)

    // ----- Workout Plans -----

    /**
     * Observes all workout plans ordered by most recently updated.
     */
    fun observeAllPlans(): Flow<List<WorkoutPlan>>

    /**
     * Observes a workout plan by its ID.
     *
     * @param id Plan ID.
     * @return A flow emitting the plan or null if not found.
     */
    fun observePlanById(id: Long): Flow<WorkoutPlan?>

    /**
     * Inserts a new workout plan.
     *
     * @return The inserted plan ID.
     */
    suspend fun addPlan(plan: WorkoutPlan): Long

    /**
     * Updates an existing workout plan.
     */
    suspend fun updatePlan(plan: WorkoutPlan)

    /**
     * Deletes a workout plan by its ID.
     *
     * @param id Plan ID to delete.
     */
    suspend fun deletePlanById(id: Long)

    // ----- Workout Type Settings -----

    /**
     * Observes enabled/disabled states for every [WorkoutType].
     *
     * @return A flow mapping each type to its enabled flag.
     */
    fun observeAllWorkoutTypeSettings(): Flow<Map<WorkoutType, Boolean>>

    /**
     * Observes the list of enabled workout types.
     */
    fun observeEnabledWorkoutTypes(): Flow<List<WorkoutType>>

    /**
     * Initializes default workout type settings if the table is empty.
     *
     * This should be called early (e.g., at app start or first entry to Home).
     */
    suspend fun initWorkoutTypeSettingsIfEmpty()

    /**
     * Updates the enabled state for a workout type.
     *
     * @param type Workout type to update.
     * @param enabled True to enable, false to disable.
     */
    suspend fun setWorkoutTypeEnabled(type: WorkoutType, enabled: Boolean)

    // ----- Preferences -----

    /**
     * Observes app-level preferences such as language and theme.
     */
    fun observePreferences(): Flow<AppPreferences>

    /**
     * Sets the UI language preference.
     *
     * Expected values:
     * - "EN"
     * - "ZH"
     */
    suspend fun setLanguage(lang: String)

    /**
     * Sets the theme preference.
     *
     * Expected values:
     * - "LIGHT"
     * - "DARK"
     */
    suspend fun setTheme(theme: String)
}