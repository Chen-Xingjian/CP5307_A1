package au.edu.jcu.fittrackplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.AppPreferences
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for app-level preferences such as language and theme.
 *
 * Responsibilities:
 * - Expose a lifecycle-aware stream of [AppPreferences] for the UI layer.
 * - Persist preference updates via [FitTrackRepository].
 */
@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val repo: FitTrackRepository
) : ViewModel() {

    /**
     * Current preferences state observed from the repository.
     *
     * - Uses [stateIn] to convert the upstream Flow to a hot [StateFlow].
     * - Starts while subscribed to avoid unnecessary work when the UI is not collecting.
     */
    val prefs: StateFlow<AppPreferences> =
        repo.observePreferences()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppPreferences()
            )

    /**
     * Persist a new language preference.
     *
     * @param lang Stable language key (e.g., "EN" or "ZH").
     */
    fun setLanguage(lang: String) {
        viewModelScope.launch { repo.setLanguage(lang) }
    }

    /**
     * Persist a new theme preference.
     *
     * @param theme Stable theme key (e.g., "LIGHT" or "DARK").
     */
    fun setTheme(theme: String) {
        viewModelScope.launch { repo.setTheme(theme) }
    }
}