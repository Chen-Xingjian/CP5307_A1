package au.edu.jcu.fittrackplus.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Exposes the current theme preference as a boolean flag for Compose.
 *
 * This ViewModel observes user preferences from [FitTrackRepository] and converts the persisted
 * theme value into a simple `darkTheme` state:
 * - `true`  -> dark theme is enabled
 * - `false` -> light theme is enabled (default)
 *
 * The flow is shared within [viewModelScope] and kept active while there are active subscribers.
 */
@HiltViewModel
class AppThemeViewModel @Inject constructor(
    repo: FitTrackRepository
) : ViewModel() {

    /**
     * Whether the app should use the dark theme.
     *
     * Backed by persisted preferences:
     * - "DARK"  -> true
     * - others  -> false
     */
    val darkTheme: StateFlow<Boolean> =
        repo.observePreferences()
            .map { it.theme == "DARK" }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
}