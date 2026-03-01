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

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    repo: FitTrackRepository
) : ViewModel() {
    val darkTheme: StateFlow<Boolean> =
        repo.observePreferences()
            .map { it.theme == "DARK" }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}