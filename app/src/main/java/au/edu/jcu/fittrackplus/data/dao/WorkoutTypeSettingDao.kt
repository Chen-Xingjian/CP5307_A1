package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.WorkoutTypeSettingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for managing which workout types are enabled/disabled in the app.
 *
 * This table backs the "Workout Type Management" screen and also drives the available
 * type options shown on Home (Quick Start) and Schedule.
 */
@Dao
interface WorkoutTypeSettingDao {

    /**
     * Observes all workout type settings.
     *
     * @return A cold [Flow] that emits whenever the settings table changes.
     */
    @Query("SELECT * FROM workout_type_settings")
    fun observeAll(): Flow<List<WorkoutTypeSettingEntity>>

    /**
     * Observes only enabled workout type settings.
     *
     * @return A cold [Flow] emitting enabled rows (enabled = 1) whenever they change.
     */
    @Query("SELECT * FROM workout_type_settings WHERE enabled = 1")
    fun observeEnabled(): Flow<List<WorkoutTypeSettingEntity>>

    /**
     * Inserts or replaces all workout type settings in a single call.
     *
     * Useful for first-run initialization or bulk updates.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<WorkoutTypeSettingEntity>)

    /**
     * Updates the enabled state for a given workout type.
     *
     * @param typeName The stable workout type identifier (usually enum name).
     * @param enabled Whether the type should be enabled (true) or disabled (false).
     */
    @Query("UPDATE workout_type_settings SET enabled = :enabled WHERE typeName = :typeName")
    suspend fun setEnabled(typeName: String, enabled: Boolean)
}