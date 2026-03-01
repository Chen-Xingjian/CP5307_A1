package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.AppointmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for workout appointments (scheduled sessions).
 *
 * Notes:
 * - This DAO exposes a reactive stream via [Flow] so UI layers can observe changes automatically.
 * - Inserts use [OnConflictStrategy.REPLACE] to overwrite an existing row with the same primary key.
 */
@Dao
interface AppointmentDao {

    /**
     * Inserts or replaces an appointment row.
     *
     * If an entity with the same primary key already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    /**
     * Observes all appointments ordered by scheduled time (ascending).
     *
     * @return A cold [Flow] that emits the full list whenever the table changes.
     */
    @Query("SELECT * FROM appointments ORDER BY scheduledTimeMillis ASC")
    fun observeAppointments(): Flow<List<AppointmentEntity>>
}