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

@HiltViewModel
class AppLocaleViewModel @Inject constructor(
    repo: FitTrackRepository
) : ViewModel() {

    val language: StateFlow<AppLanguage> =
        repo.observePreferences()
            .map { AppLanguage.fromCode(it.language) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.EN)
}