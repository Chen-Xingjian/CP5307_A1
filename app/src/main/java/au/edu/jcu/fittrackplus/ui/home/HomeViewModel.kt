package au.edu.jcu.fittrackplus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.fittrackplus.domain.model.WorkoutRecord
import au.edu.jcu.fittrackplus.domain.repository.FitTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: FitTrackRepository
) : ViewModel() {

    val latestRecord: StateFlow<WorkoutRecord?> =
        repository.observeLatestRecord()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}