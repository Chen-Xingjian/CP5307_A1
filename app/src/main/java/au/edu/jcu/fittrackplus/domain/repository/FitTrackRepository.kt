package au.edu.jcu.fittrackplus.domain.repository

import au.edu.jcu.fittrackplus.domain.model.Appointment
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import kotlinx.coroutines.flow.Flow

interface FitTrackRepository {
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)

    fun observeLatestRecord(): Flow<WorkoutRecord?>
    fun observeAllRecords(): Flow<List<WorkoutRecord>>
    suspend fun addRecord(record: WorkoutRecord)

    fun observeAppointments(): Flow<List<Appointment>>
    suspend fun addAppointment(appointment: Appointment)
}