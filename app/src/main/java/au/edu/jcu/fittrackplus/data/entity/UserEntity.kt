package au.edu.jcu.fittrackplus.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val gender: String,
    val age: Int,
    val heightCm: Double,
    val weightKg: Double,
    val preferredExercise: String
)