package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings

/**
 * Settings hub screen.
 *
 * Responsibilities:
 * - Provides navigation entry points to Profile, Workout Type Management, and Preferences.
 * - Keeps UI copy localized through [LocalStrings] (except short helper subtitles).
 *
 * Note:
 * - This screen does not own any state; it only dispatches navigation callbacks.
 */
@Composable
fun SettingsScreen(
    onGoProfile: () -> Unit,
    onGoCategory: () -> Unit,
    onGoPreferences: () -> Unit
) {
    val s = LocalStrings.current

    // Surface uses the app theme background and ensures consistent padding/contrast.
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screen title.
            Text(
                text = s.settingsTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Short description under the title.
            Text(
                text = if (s.isZh) "管理个人信息与偏好设置" else "Manage your profile and preferences",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            // Entry: Profile.
            SettingEntry(
                title = s.personalInfo,
                subtitle = if (s.isZh) "身高体重与基础资料" else "Body stats and basic info",
                icon = Icons.Default.Person,
                onClick = onGoProfile
            )

            // Entry: Workout type management.
            SettingEntry(
                title = s.workoutTypeManagement,
                subtitle = if (s.isZh) "启用/隐藏运动种类" else "Enable or hide workout types",
                icon = Icons.Default.FitnessCenter,
                onClick = onGoCategory
            )

            // Entry: Preferences.
            SettingEntry(
                title = s.preferences,
                subtitle = if (s.isZh) "语言与主题外观" else "Language and theme",
                icon = Icons.Default.Tune,
                onClick = onGoPreferences
            )
        }
    }
}

/**
 * A reusable row-style settings entry.
 *
 * UI structure:
 * - Leading icon inside a colored badge
 * - Title and subtitle
 * - Trailing chevron to indicate navigation
 *
 * @param title Primary label of the entry.
 * @param subtitle Secondary description shown under the title.
 * @param icon Leading icon displayed in a badge.
 * @param onClick Navigation callback triggered when the card is tapped.
 */
@Composable
private fun SettingEntry(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon badge.
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Decorative; title already describes the destination.
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }

            Spacer(Modifier.size(12.dp))

            // Labels.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Trailing chevron indicates a navigation destination.
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null, // Decorative.
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}