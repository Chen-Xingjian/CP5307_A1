package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
    val prefs by vm.prefs.collectAsStateWithLifecycle()

    var langExpanded by rememberSaveable { mutableStateOf(false) }
    var themeExpanded by rememberSaveable { mutableStateOf(false) }

    // Language 显示：中文/English 更直观
    val langDisplay = if (prefs.language == "ZH") "中文" else "English"

    // Theme 显示：用 i18n
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---- Language ----
            Box {
                OutlinedTextField(
                    value = langDisplay,
                    onValueChange = {},
                    readOnly = true,
                    enabled = true, // ✅ 关键：保持正常黑边框样式
                    label = { Text(s.language) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 覆盖透明点击层：点击展开
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

            // ---- Theme ----
            Box {
                OutlinedTextField(
                    value = themeDisplay,
                    onValueChange = {},
                    readOnly = true,
                    enabled = true, // ✅ 关键：保持正常黑边框样式
                    label = { Text(s.theme) },
                    modifier = Modifier.fillMaxWidth()
                )

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
}