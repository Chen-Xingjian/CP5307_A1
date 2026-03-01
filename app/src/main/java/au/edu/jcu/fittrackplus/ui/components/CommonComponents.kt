package au.edu.jcu.fittrackplus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A simple clickable card used as an entry point to a feature screen.
 *
 * Typical usage:
 * - Settings menu items (Profile, Preferences, Workout Type Management)
 * - Any lightweight navigation list where each row is a card
 *
 * Note:
 * - This component only handles UI rendering and click forwarding.
 * - Navigation logic should remain outside (passed via [onClick]).
 */
@Composable
fun FeatureEntryCard(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp)
        )
    }
}