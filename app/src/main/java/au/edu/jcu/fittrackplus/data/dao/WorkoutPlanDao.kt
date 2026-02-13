package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import au.edu.jcu.fittrackplus.data.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {

    @Query("SELECT * FROM workout_plan ORDER BY updatedAt DESC")
    fun observeAllPlans(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT * FROM workout_plan WHERE id = :id LIMIT 1")
    fun observePlanById(id: Long): Flow<WorkoutPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: WorkoutPlanEntity): Long

    @Update
    suspend fun update(plan: WorkoutPlanEntity)
}