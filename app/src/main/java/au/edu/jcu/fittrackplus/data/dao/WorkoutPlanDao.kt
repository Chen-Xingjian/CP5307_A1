package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import au.edu.jcu.fittrackplus.data.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for CRUD operations on workout plans.
 *
 * Workout plans are used by the Schedule feature to create, edit, list, and delete presets.
 */
@Dao
interface WorkoutPlanDao {

    /**
     * Observes all workout plans ordered by the last updated timestamp (descending).
     *
     * @return A cold [Flow] emitting the latest list of plans whenever the table changes.
     */
    @Query("SELECT * FROM workout_plan ORDER BY updatedAt DESC")
    fun observeAllPlans(): Flow<List<WorkoutPlanEntity>>

    /**
     * Observes a single workout plan by its id.
     *
     * @param id The plan primary key.
     * @return A cold [Flow] emitting the plan entity, or null if it does not exist.
     */
    @Query("SELECT * FROM workout_plan WHERE id = :id LIMIT 1")
    fun observePlanById(id: Long): Flow<WorkoutPlanEntity?>

    /**
     * Inserts a workout plan.
     *
     * If a row with the same primary key exists, it will be replaced.
     *
     * @return The newly inserted row id.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: WorkoutPlanEntity): Long

    /**
     * Updates an existing workout plan.
     *
     * The target row is matched by the entity primary key.
     */
    @Update
    suspend fun update(plan: WorkoutPlanEntity)

    /**
     * Deletes a workout plan by its id.
     *
     * @param id The plan primary key.
     */
    @Query("DELETE FROM workout_plan WHERE id = :id")
    suspend fun deleteById(id: Long)
}