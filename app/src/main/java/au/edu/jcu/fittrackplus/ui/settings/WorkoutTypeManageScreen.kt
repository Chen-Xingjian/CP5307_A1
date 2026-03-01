package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTypeManageScreen(
    onBack: () -> Unit,
    vm: WorkoutTypeManageViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val ui by vm.ui.collectAsStateWithLifecycle()
    var filterExpanded by rememberSaveable { mutableStateOf(false) }

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
            // ===== Filter dropdown (styled, still same logic) =====
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

            // ===== List =====
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
            onValueChange = {},
            readOnly = true,
            enabled = true, // keep normal border
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
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
            containerColor = if (enabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surface
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