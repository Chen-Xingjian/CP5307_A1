package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val ui by vm.ui.collectAsStateWithLifecycle()

    var genderExpanded by rememberSaveable { mutableStateOf(false) }
    val genderOptions = listOf("male", "female", "other")

    fun genderDisplay(value: String): String {
        return when (value) {
            "male" -> s.male
            "female" -> s.female
            else -> s.other
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.personalInfoTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = ui.name,
                onValueChange = vm::setName,
                label = { Text(s.name) },
                modifier = Modifier.fillMaxWidth()
            )

            // ✅ gender dropdown：保持黑边框（enabled=true），但不可输入（readOnly=true）
            Box {
                val currentGender = ui.gender.ifBlank { "male" }

                OutlinedTextField(
                    value = genderDisplay(currentGender),
                    onValueChange = {},
                    readOnly = true,
                    enabled = true, // ✅ 关键：不要 false
                    label = { Text(s.gender) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 覆盖层接管点击
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { genderExpanded = true }
                )

                DropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    genderOptions.forEach { g ->
                        DropdownMenuItem(
                            text = { Text(genderDisplay(g)) },
                            onClick = {
                                vm.setGender(g) // 保存仍用 "male/female/other"
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = ui.age,
                onValueChange = vm::setAge,
                label = { Text(s.age) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ui.heightCm,
                onValueChange = vm::setHeightCm,
                label = { Text(s.heightCm) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ui.weightKg,
                onValueChange = vm::setWeightKg,
                label = { Text(s.weightKg) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            ui.message?.let { Text(it) }

            Button(
                onClick = vm::saveProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(s.save)
            }
        }
    }
}