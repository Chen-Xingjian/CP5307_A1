package au.edu.jcu.fittrackplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.AppPreferences
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val repo: FitTrackRepository
) : ViewModel() {

    val prefs: StateFlow<AppPreferences> =
        repo.observePreferences().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences())

    fun setLanguage(lang: String) {
        viewModelScope.launch { repo.setLanguage(lang) }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { repo.setTheme(theme) }
    }
}