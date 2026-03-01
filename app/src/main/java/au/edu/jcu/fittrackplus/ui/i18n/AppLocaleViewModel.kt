package au.edu.jcu.fittrackplus.ui.i18n

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
 * Exposes the current application language as a reactive state.
 *
 * This ViewModel reads the persisted language preference from [FitTrackRepository]
 * and maps it to [AppLanguage] for UI consumption.
 */
@HiltViewModel
class AppLocaleViewModel @Inject constructor(
    repo: FitTrackRepository
) : ViewModel() {

    /**
     * Current language selection as a hot [StateFlow].
     *
     * The value is derived from repository preferences and defaults to [AppLanguage.EN].
     */
    val language: StateFlow<AppLanguage> =
        repo.observePreferences()
            .map { AppLanguage.fromCode(it.language) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.EN)
}