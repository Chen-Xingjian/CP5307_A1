package au.edu.jcu.fittrackplus.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.UserProfile
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val gender: String = "",
    val age: String = "25",
    val heightCm: String = "170",
    val weightKg: String = "70",
    val preferredExercise: String = "RUNNING",
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeUserProfile().collect { profile ->
                if (profile != null) {
                    _ui.update {
                        it.copy(
                            name = profile.name,
                            gender = profile.gender,
                            age = profile.age.toString(),
                            heightCm = profile.heightCm.toString(),
                            weightKg = profile.weightKg.toString(),
                            preferredExercise = profile.preferredExercise
                        )
                    }
                }
            }
        }
    }

    fun setName(v: String) = _ui.update { it.copy(name = v, error = null) }
    fun setGender(v: String) = _ui.update { it.copy(gender = v, error = null) }
    fun setAge(v: String) = _ui.update { it.copy(age = v, error = null) }
    fun setHeightCm(v: String) = _ui.update { it.copy(heightCm = v, error = null) }
    fun setWeightKg(v: String) = _ui.update { it.copy(weightKg = v, error = null) }
    fun setPreferredExercise(v: String) = _ui.update { it.copy(preferredExercise = v, error = null) }

    fun saveProfile() {
        val state = _ui.value

        val age = state.age.toIntOrNull()
        val height = state.heightCm.toDoubleOrNull()
        val weight = state.weightKg.toDoubleOrNull()

        if (age == null || age <= 0) {
            _ui.update { it.copy(error = "Please input a valid age.") }
            return
        }
        if (height == null || height <= 0) {
            _ui.update { it.copy(error = "Please input a valid height.") }
            return
        }
        if (weight == null || weight <= 0) {
            _ui.update { it.copy(error = "Please input a valid weight.") }
            return
        }

        val profile = UserProfile(
            id = 1,
            name = state.name.trim(),
            gender = state.gender.trim(),
            age = age,
            heightCm = height,
            weightKg = weight,
            preferredExercise = state.preferredExercise.trim().ifBlank { "RUNNING" }
        )

        viewModelScope.launch {
            repository.saveUserProfile(profile)
            _ui.update {
                it.copy(
                    error = null,
                    message = "Profile saved."
                )
            }
        }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null) }
    }

    fun clearError() {
        _ui.update { it.copy(error = null) }
    }
}