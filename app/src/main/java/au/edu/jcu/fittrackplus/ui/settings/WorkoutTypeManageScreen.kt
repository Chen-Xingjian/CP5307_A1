package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

/**
 * Workout Type Management screen.
 *
 * Responsibilities:
 * - Displays enabled/disabled state for each [WorkoutType].
 * - Provides a filter dropdown for viewing all / applied / not applied types.
 * - Delegates all state changes to [WorkoutTypeManageViewModel].
 *
 * Note:
 * - This screen does not persist UI state directly; persistence is handled by the ViewModel/repository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTypeManageScreen(
    onBack: () -> Unit,
    vm: WorkoutTypeManageViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val ui by vm.ui.collectAsStateWithLifecycle()

    // Controls the visibility of the filter dropdown menu.
    var filterExpanded by rememberSaveable { mutableStateOf(false) }

    // Ensures default settings exist (only runs once per composition entry).
    LaunchedEffect(Unit) { vm.initIfNeeded() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.workoutTypesTitle) },
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
            // Filter dropdown (UI-only styling; filtering logic remains in ViewModel).
            FilterDropdown(
                label = s.filter,
                value = when (ui.filter) {
                    TypeFilter.ALL -> s.all
                    TypeFilter.APPLIED -> s.applied
                    TypeFilter.NOT_APPLIED -> s.notApplied
                },
                expanded = filterExpanded,
                onExpand = { filterExpanded = true },
                onDismiss = { filterExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.all) },
                    onClick = { vm.setFilter(TypeFilter.ALL); filterExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(s.applied) },
                    onClick = { vm.setFilter(TypeFilter.APPLIED); filterExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text(s.notApplied) },
                    onClick = { vm.setFilter(TypeFilter.NOT_APPLIED); filterExpanded = false }
                )
            }

            // List of types and their enabled state.
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(ui.list, key = { it.first.name }) { (type, enabled) ->
                    TypeRow(
                        type = type,
                        enabled = enabled,
                        onToggle = { vm.toggle(type, !enabled) }
                    )
                }
            }
        }
    }
}

/**
 * Reusable dropdown field for filtering.
 *
 * Implementation notes:
 * - Uses an [OutlinedTextField] for a consistent "form" look.
 * - Uses [MutableInteractionSource] with a custom clickable modifier to make the field act like a menu anchor.
 * - Keeps `enabled = true` so the outline/border uses the normal (non-disabled) style.
 *
 * @param label Field label text.
 * @param value Currently selected value shown in the field.
 * @param expanded Whether the dropdown menu is visible.
 * @param onExpand Callback to open the dropdown menu.
 * @param onDismiss Callback to close the dropdown menu.
 * @param menuContent Menu items content.
 */
@Composable
private fun FilterDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {}, // Read-only field; selection happens via dropdown.
            readOnly = true,
            enabled = true, // Keeps the normal outline (not "disabled" grey).
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null // Decorative indicator only.
                )
            },
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onExpand() }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            content = menuContent
        )
    }
}

/**
 * A single row representing one [WorkoutType] and its "enabled" state.
 *
 * UI rules:
 * - Enabled types get a subtle highlighted background.
 * - Button label switches between "Apply" and "Cancel Apply" based on current state.
 *
 * @param type The workout type being displayed.
 * @param enabled Current enabled state.
 * @param onToggle Action to toggle the enabled state.
 */
@Composable
private fun TypeRow(
    type: WorkoutType,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                // Subtle emphasis for enabled types; alpha keeps it light and consistent.
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type.localizedName(isZh),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (enabled) s.applied else s.notApplied,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = onToggle,
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (enabled) s.cancelApply else s.apply)
            }
        }
    }
}