package au.edu.jcu.fittrackplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.fittrackplus.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for the single user profile stored in the local database.
 *
 * The app uses a fixed primary key (id = 1) to represent the current user's profile.
 */
@Dao
interface UserDao {

    /**
     * Observes the current user profile (id = 1).
     *
     * @return A cold [Flow] emitting the profile entity, or null if it does not exist yet.
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeUser(): Flow<UserEntity?>

    /**
     * Inserts or updates the user profile.
     *
     * If a row with the same primary key already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)
}