package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings

@Composable
fun SettingsScreen(
    onGoProfile: () -> Unit,
    onGoCategory: () -> Unit,
    onGoPreferences: () -> Unit
) {
    val s = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(s.settingsTitle, style = MaterialTheme.typography.headlineSmall)

        SettingEntry(s.personalInfo) { onGoProfile() }
        SettingEntry(s.workoutTypeManagement) { onGoCategory() }
        SettingEntry(s.preferences) { onGoPreferences() }
    }
}

@Composable
private fun SettingEntry(title: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Text(
            title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}