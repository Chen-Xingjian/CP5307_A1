package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.AppPreferenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for app-level preferences stored as simple key-value pairs.
 *
 * Typical keys include language and theme settings.
 */
@Dao
interface AppPreferenceDao {

    /**
     * Observes all stored preferences.
     *
     * @return A cold [Flow] that emits the full preference list whenever the table changes.
     */
    @Query("SELECT * FROM app_preferences")
    fun observeAll(): Flow<List<AppPreferenceEntity>>

    /**
     * Inserts or replaces a preference entry by its primary key.
     *
     * If a preference with the same key already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pref: AppPreferenceEntity)
}