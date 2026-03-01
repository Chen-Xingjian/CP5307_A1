package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.WorkoutTypeSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTypeSettingDao {

    @Query("SELECT * FROM workout_type_settings")
    fun observeAll(): Flow<List<WorkoutTypeSettingEntity>>

    @Query("SELECT * FROM workout_type_settings WHERE enabled = 1")
    fun observeEnabled(): Flow<List<WorkoutTypeSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<WorkoutTypeSettingEntity>)

    @Query("UPDATE workout_type_settings SET enabled = :enabled WHERE typeName = :typeName")
    suspend fun setEnabled(typeName: String, enabled: Boolean)
}