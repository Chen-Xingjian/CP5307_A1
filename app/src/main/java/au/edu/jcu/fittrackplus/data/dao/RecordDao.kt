package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for workout history records.
 *
 * Records are stored in [workout_records] and are typically ordered by start time (newest first).
 */
@Dao
interface RecordDao {

    /**
     * Inserts a workout record.
     *
     * If a record with the same primary key exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WorkoutRecordEntity)

    /**
     * Observes all workout records ordered by [WorkoutRecordEntity.startTimeMillis] descending.
     *
     * @return A cold [Flow] that emits the full list whenever the table changes.
     */
    @Query("SELECT * FROM workout_records ORDER BY startTimeMillis DESC")
    fun observeAllRecords(): Flow<List<WorkoutRecordEntity>>

    /**
     * Observes the latest workout record by [WorkoutRecordEntity.startTimeMillis].
     *
     * @return A cold [Flow] emitting the newest record, or null if no records exist.
     */
    @Query("SELECT * FROM workout_records ORDER BY startTimeMillis DESC LIMIT 1")
    fun observeLatestRecord(): Flow<WorkoutRecordEntity?>

    /**
     * Deletes a workout record by its id.
     */
    @Query("DELETE FROM workout_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}