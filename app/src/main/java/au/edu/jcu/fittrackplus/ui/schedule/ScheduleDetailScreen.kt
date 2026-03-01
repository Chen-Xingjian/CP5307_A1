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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

/**
 * Screen for viewing and editing an existing workout plan.
 *
 * UI-only screen:
 * - Default state is read-only (fields appear "disabled" to indicate non-editable).
 * - Tapping the Edit action enables fields and allows updating plan details.
 * - Category is selectable only in edit mode via a dropdown.
 *
 * Business logic is handled by [ScheduleViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    planId: Long,
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    // Observed plan and form state (source of truth is the ViewModel).
    val selected by vm.selectedPlan.collectAsState()
    val form by vm.form.collectAsState()

    // Controls edit mode for the entire screen.
    var editing by rememberSaveable { mutableStateOf(false) }

    // Controls category dropdown expansion.
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Bind the selected plan from the repository by ID.
    LaunchedEffect(planId) { vm.bindPlanById(planId) }

    // Populate the editable form whenever the selected plan changes.
    LaunchedEffect(selected?.id) { selected?.let { vm.loadToForm(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.planDetailTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } },
                actions = {
                    // Toggle edit mode. Closing edit mode also collapses the dropdown.
                    TextButton(
                        onClick = {
                            editing = !editing
                            if (!editing) expanded = false
                        }
                    ) {
                        Text(if (editing) s.cancel else s.edit)
                    }
                }
            )
        }
    ) { inner ->
        // Show a loading indicator until the plan is available.
        if (selected == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== Detail Card =====
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
                    // Plan name:
                    // - Disabled (greyed) when not editing to indicate read-only state.
                    // - Enabled when editing to allow changes.
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = vm::onNameChange,
                        label = { Text(s.planName) },
                        enabled = editing,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category selector:
                    // - Disabled when not editing.
                    // - Enabled when editing, and tapping opens the dropdown.
                    Box {
                        OutlinedTextField(
                            value = form.selectedType.localizedName(isZh),
                            onValueChange = {},
                            readOnly = true,
                            enabled = editing,
                            label = { Text(s.category) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (editing) Modifier.clickable { expanded = true } else Modifier
                                )
                        )

                        DropdownMenu(
                            expanded = expanded && editing,
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

                    // Duration and calories grouped in one row for a compact layout.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = form.durationMinutes,
                            onValueChange = vm::onDurationChange,
                            label = { Text(s.durationMinutes) },
                            enabled = editing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = form.estimatedCalories,
                            onValueChange = vm::onCaloriesChange,
                            label = { Text(s.estimatedCalories) },
                            enabled = editing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Optional note field.
                    OutlinedTextField(
                        value = form.note,
                        onValueChange = vm::onNoteChange,
                        label = { Text(s.planNote) },
                        enabled = editing,
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Validation / error message coming from the ViewModel.
                    form.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Push the Save button to the bottom.
            Spacer(Modifier.weight(1f))

            // Show a single primary action only while editing.
            if (editing) {
                Button(
                    // Persist changes through the ViewModel and then exit edit mode.
                    onClick = { vm.updatePlan { editing = false; expanded = false } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(s.saveChanges, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}