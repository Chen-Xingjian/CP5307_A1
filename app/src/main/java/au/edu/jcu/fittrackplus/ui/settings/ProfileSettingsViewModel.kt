package au.edu.jcu.fittrackplus.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val gender: String = "", // "male" / "female" / "other"
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "", // allow decimal input

    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui.asStateFlow()

    // ✅ 关键：根据你的报错，UserProfile.id 期望是 Int
    private var profileId: Int = 1
    private var preferredExercise: String = ""

    init {
        viewModelScope.launch {
            repository.observeUserProfile().collectLatest { profile ->
                if (profile == null) return@collectLatest

                profileId = profile.id
                preferredExercise = profile.preferredExercise

                _ui.update {
                    it.copy(
                        name = profile.name,
                        gender = profile.gender,
                        age = profile.age.takeIf { a -> a > 0 }?.toString() ?: "",
                        heightCm = profile.heightCm.takeIf { h -> h > 0 }?.toString() ?: "",
                        weightKg = profile.weightKg.takeIf { w -> w > 0 }?.toString() ?: "",
                        error = null
                    )
                }
            }
        }
    }

    fun setName(v: String) = _ui.update { it.copy(name = v, error = null, message = null) }

    fun setGender(v: String) {
        val normalized = when (v.lowercase()) {
            "male" -> "male"
            "female" -> "female"
            else -> "other"
        }
        _ui.update { it.copy(gender = normalized, error = null, message = null) }
    }

    fun setAge(v: String) {
        val filtered = v.filter(Char::isDigit).take(3)
        _ui.update { it.copy(age = filtered, error = null, message = null) }
    }

    fun setHeightCm(v: String) {
        val filtered = v.filter(Char::isDigit).take(3)
        _ui.update { it.copy(heightCm = filtered, error = null, message = null) }
    }

    fun setWeightKg(v: String) {
        // allow digits + one dot
        val cleaned = buildString {
            var dotUsed = false
            for (ch in v) {
                when {
                    ch.isDigit() -> append(ch)
                    ch == '.' && !dotUsed -> {
                        dotUsed = true
                        append(ch)
                    }
                }
            }
        }.take(6)
        _ui.update { it.copy(weightKg = cleaned, error = null, message = null) }
    }

    fun saveProfile() {
        val state = _ui.value

        val name = state.name.trim()
        val gender = state.gender.ifBlank { "male" }
        val ageInt = state.age.toIntOrNull() ?: 0
        val heightDouble = state.heightCm.toDoubleOrNull() ?: 0.0
        val weightDouble = state.weightKg.toDoubleOrNull() ?: 0.0

        val err = when {
            name.isBlank() -> "Name is required."
            ageInt !in 1..120 -> "Age must be 1–120."
            heightDouble !in 50.0..250.0 -> "Height must be 50–250 cm."
            weightDouble !in 20.0..300.0 -> "Weight must be 20–300 kg."
            else -> null
        }

        if (err != null) {
            _ui.update { it.copy(error = err, message = null) }
            return
        }

        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfile(
                    id = profileId,                // ✅ Int
                    name = name,
                    gender = gender,
                    age = ageInt,
                    heightCm = heightDouble,
                    weightKg = weightDouble,        // ✅ Double
                    preferredExercise = preferredExercise
                )
            )
            _ui.update { it.copy(error = null, message = "Saved.") }
        }
    }
}