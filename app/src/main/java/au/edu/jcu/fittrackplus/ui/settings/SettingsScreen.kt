package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.edu.jcu.fittrackplus.ui.profile.ProfileContent

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        // 这里可以放主题、语言开关
        Text("General Settings")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Profile", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileContent()
    }
}