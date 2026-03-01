package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

/**
 * Screen for creating a new workout plan.
 *
 * UI-only screen:
 * - Keeps the form layout in a single card for a clean, app-like look.
 * - Uses a dropdown for workout category selection.
 * - Submits via a bottom-aligned primary action button.
 *
 * Business logic is handled by [ScheduleViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCreateScreen(
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    // Collect the current form state from the ViewModel.
    val form by vm.form.collectAsState()

    // Controls the category dropdown menu state.
    var expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.newPlanTitle) },
                navigationIcon = {
                    // Use a simple text action for back navigation to match existing app style.
                    TextButton(onClick = onBack) { Text(s.back) }
                }
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
            // ===== Main Form Card =====
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
                    // Plan name input (single line).
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = vm::onNameChange,
                        label = { Text(s.planName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category dropdown:
                    // - Keep enabled=true to preserve the normal outlined border style.
                    // - Use a full-size click overlay to open the dropdown.
                    Box {
                        OutlinedTextField(
                            value = form.selectedType.localizedName(isZh),
                            onValueChange = {},
                            readOnly = true,
                            enabled = true,
                            label = { Text(s.category) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Click overlay to make the entire field behave like a selector.
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expanded = true }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            form.categoryOptions.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.localizedName(isZh)) },
                                    onClick = {
                                        vm.onTypeChange(t)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Duration + Calories grouped on one row for a compact, app-like form layout.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = form.durationMinutes,
                            onValueChange = vm::onDurationChange,
                            label = { Text(s.durationMinutes) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = form.estimatedCalories,
                            onValueChange = vm::onCaloriesChange,
                            label = { Text(s.estimatedCalories) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Optional note (multi-line).
                    OutlinedTextField(
                        value = form.note,
                        onValueChange = vm::onNoteChange,
                        label = { Text(s.planNote) },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Validation / error message coming from ViewModel.
                    form.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Push the primary button to the bottom of the screen.
            Spacer(Modifier.weight(1f))

            // ===== Bottom Primary Button =====
            // Triggers plan creation through the ViewModel and returns on success.
            Button(
                onClick = { vm.createPlan(onSuccess = onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(s.save, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}