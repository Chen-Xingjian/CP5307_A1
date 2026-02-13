package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WorkoutRecordEntity)

    @Query("SELECT * FROM workout_records ORDER BY startTimeMillis DESC")
    fun observeAllRecords(): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT * FROM workout_records ORDER BY startTimeMillis DESC LIMIT 1")
    fun observeLatestRecord(): Flow<WorkoutRecordEntity?>
}