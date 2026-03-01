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

/**
 * UI state for the Profile Settings screen.
 *
 * Notes:
 * - All numeric values are stored as strings to support partial user input and validation.
 * - [gender] is stored as a stable key ("male" / "female" / "other") and localized in the UI.
 */
data class ProfileUiState(
    val name: String = "",
    val gender: String = "", // "male" / "female" / "other"
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "", // Allows decimal input.

    val error: String? = null,
    val message: String? = null
)

/**
 * ViewModel for managing user profile input and persistence.
 *
 * Responsibilities:
 * - Load the existing profile from [FitTrackRepository] and map it into [ProfileUiState].
 * - Validate user input and persist changes back to the repository.
 * - Keep stable identifiers such as [profileId] and [preferredExercise] intact when saving.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FitTrackRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui.asStateFlow()

    // Persisted identity fields that should not be lost during editing.
    private var profileId: Int = 1
    private var preferredExercise: String = ""

    init {
        // Observe the stored profile and update UI state whenever it changes.
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
                        error = null,
                        message = null
                    )
                }
            }
        }
    }

    /** Updates the name field and clears any previous messages. */
    fun setName(v: String) = _ui.update { it.copy(name = v, error = null, message = null) }

    /**
     * Updates the gender field using stable normalized keys.
     *
     * @param v A value coming from UI selection. Any unexpected value maps to "other".
     */
    fun setGender(v: String) {
        val normalized = when (v.lowercase()) {
            "male" -> "male"
            "female" -> "female"
            else -> "other"
        }
        _ui.update { it.copy(gender = normalized, error = null, message = null) }
    }

    /**
     * Updates age as a numeric string (max 3 digits).
     * The final validation happens in [saveProfile].
     */
    fun setAge(v: String) {
        val filtered = v.filter(Char::isDigit).take(3)
        _ui.update { it.copy(age = filtered, error = null, message = null) }
    }

    /**
     * Updates height (cm) as a numeric string (max 3 digits).
     * The final validation happens in [saveProfile].
     */
    fun setHeightCm(v: String) {
        val filtered = v.filter(Char::isDigit).take(3)
        _ui.update { it.copy(heightCm = filtered, error = null, message = null) }
    }

    /**
     * Updates weight (kg) as a numeric string allowing a single decimal separator.
     * The final validation happens in [saveProfile].
     */
    fun setWeightKg(v: String) {
        // Allows digits plus a single dot for decimals.
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

    /**
     * Validates the current UI state and persists it as a [UserProfile].
     *
     * Validation rules:
     * - Name is required.
     * - Age: 1–120
     * - Height: 50–250 cm
     * - Weight: 20–300 kg
     */
    fun saveProfile() {
        val state = _ui.value

        val name = state.name.trim()
        val gender = state.gender.ifBlank { "male" }
        val ageInt = state.age.toIntOrNull() ?: 0
        val heightDouble = state.heightCm.toDoubleOrNull() ?: 0.0
        val weightDouble = state.weightKg.toDoubleOrNull() ?: 0.0

        // User-facing validation message (kept simple and explicit).
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

        // Save to repository using stable identity fields and validated values.
        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfile(
                    id = profileId,
                    name = name,
                    gender = gender,
                    age = ageInt,
                    heightCm = heightDouble,
                    weightKg = weightDouble,
                    preferredExercise = preferredExercise
                )
            )
            _ui.update { it.copy(error = null, message = "Saved.") }
        }
    }
}