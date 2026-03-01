package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.profile.ProfileViewModel

/**
 * Profile settings screen.
 *
 * Allows users to update personal information used for calorie estimation
 * (e.g., weight, height, and age).
 *
 * Note:
 * - This screen intentionally keeps the business logic in [ProfileViewModel].
 * - Dropdown fields use an overlay click target to preserve the OutlinedTextField look
 *   while preventing direct text input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val ui by vm.ui.collectAsStateWithLifecycle()

    // Controls the visibility of the gender dropdown menu.
    var genderExpanded by rememberSaveable { mutableStateOf(false) }

    // Stable keys persisted to storage.
    val genderOptions = listOf("male", "female", "other")

    /**
     * Maps a persisted gender key to a localized label.
     *
     * @param value Persisted gender key ("male" / "female" / "other").
     */
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Helper text shown at the top of the form.
            Text(
                text = if (s.isZh) {
                    "完善资料用于更准确的消耗估算"
                } else {
                    "Complete your profile for better calorie estimation"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Name
                    OutlinedTextField(
                        value = ui.name,
                        onValueChange = vm::setName,
                        label = { Text(s.name) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Gender dropdown:
                    // - enabled=true keeps the normal OutlinedTextField border styling.
                    // - readOnly=true prevents keyboard input.
                    // - an overlay box captures clicks to open the menu.
                    Box {
                        val currentGender = ui.gender.ifBlank { "male" }

                        OutlinedTextField(
                            value = genderDisplay(currentGender),
                            onValueChange = {},
                            readOnly = true,
                            enabled = true,
                            label = { Text(s.gender) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Transparent overlay to handle clicks anywhere on the field.
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
                                        // Persist the stable gender key ("male" / "female" / "other").
                                        vm.setGender(g)
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Age
                    OutlinedTextField(
                        value = ui.age,
                        onValueChange = vm::setAge,
                        label = { Text(s.age) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Height (cm)
                    OutlinedTextField(
                        value = ui.heightCm,
                        onValueChange = vm::setHeightCm,
                        label = { Text(s.heightCm) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Weight (kg)
                    OutlinedTextField(
                        value = ui.weightKg,
                        onValueChange = vm::setWeightKg,
                        label = { Text(s.weightKg) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Inline feedback from the ViewModel.
                    ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    ui.message?.let { Text(it) }

                    // Persist profile changes.
                    Button(
                        onClick = vm::saveProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(s.save)
                    }
                }
            }
        }
    }
}