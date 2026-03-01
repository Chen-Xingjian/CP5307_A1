package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    onBack: () -> Unit,
    vm: PreferencesViewModel = hiltViewModel()
) {
    val s = LocalStrings.current

    // Collect the current preferences from the ViewModel with lifecycle awareness.
    val prefs by vm.prefs.collectAsStateWithLifecycle()

    // Dropdown expansion states (persisted across configuration changes).
    var langExpanded by rememberSaveable { mutableStateOf(false) }
    var themeExpanded by rememberSaveable { mutableStateOf(false) }

    // Human-friendly display values for current selections.
    // Note: Stored values remain stable keys (e.g., "EN"/"ZH", "LIGHT"/"DARK").
    val langDisplay = if (prefs.language == "ZH") "中文" else "English"
    val themeDisplay = if (prefs.theme == "DARK") s.dark else s.light

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.preferencesTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Lightweight helper copy for the page header.
            // (UI text only; does not affect any preference logic.)
            Text(
                text = if (s.isZh) "选择语言与主题偏好" else "Choose language & theme preferences",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Settings container card (visual grouping only).
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // -----------------------------
                    // Language dropdown (read-only field + click overlay).
                    // IMPORTANT: enabled=true keeps the normal outlined border style.
                    // -----------------------------
                    Box {
                        OutlinedTextField(
                            value = langDisplay,
                            onValueChange = {},
                            readOnly = true,
                            enabled = true, // Keep a normal outlined border (avoid "disabled" grey styling).
                            label = { Text(s.language) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Transparent overlay captures taps without making the TextField editable.
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { langExpanded = true }
                        )

                        DropdownMenu(
                            expanded = langExpanded,
                            onDismissRequest = { langExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("中文") },
                                onClick = {
                                    // Persist language using stable keys.
                                    vm.setLanguage("ZH")
                                    langExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("English") },
                                onClick = {
                                    vm.setLanguage("EN")
                                    langExpanded = false
                                }
                            )
                        }
                    }

                    // -----------------------------
                    // Theme dropdown (read-only field + click overlay).
                    // IMPORTANT: enabled=true keeps the normal outlined border style.
                    // -----------------------------
                    Box {
                        OutlinedTextField(
                            value = themeDisplay,
                            onValueChange = {},
                            readOnly = true,
                            enabled = true, // Keep a normal outlined border (avoid "disabled" grey styling).
                            label = { Text(s.theme) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Transparent overlay captures taps without making the TextField editable.
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { themeExpanded = true }
                        )

                        DropdownMenu(
                            expanded = themeExpanded,
                            onDismissRequest = { themeExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(s.light) },
                                onClick = {
                                    // Persist theme using stable keys.
                                    vm.setTheme("LIGHT")
                                    themeExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(s.dark) },
                                onClick = {
                                    vm.setTheme("DARK")
                                    themeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Additional hint text (UI-only).
            Text(
                text = if (s.isZh)
                    "提示：主题与语言会保存到偏好设置。"
                else
                    "Tip: Language & theme will be saved to preferences.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}