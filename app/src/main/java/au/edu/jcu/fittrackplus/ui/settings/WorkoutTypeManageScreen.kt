package au.edu.jcu.fittrackplus.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ✅ Filter dropdown (NOT disabled => keep normal border)
            Box {
                val filterText = when (ui.filter) {
                    TypeFilter.ALL -> s.all
                    TypeFilter.APPLIED -> s.applied
                    TypeFilter.NOT_APPLIED -> s.notApplied
                }

                val interactionSource = remember { MutableInteractionSource() }

                OutlinedTextField(
                    value = filterText,
                    onValueChange = {},
                    readOnly = true,
                    enabled = true, // ✅ keep normal style
                    label = { Text(s.filter) },
                    trailingIcon = {
                        Icon(
                            imageVector = if (filterExpanded)
                                Icons.Filled.KeyboardArrowUp
                            else
                                Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { filterExpanded = true }
                )

                DropdownMenu(
                    expanded = filterExpanded,
                    onDismissRequest = { filterExpanded = false }
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
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ui.list, key = { it.first.name }) { (type, enabled) ->
                    TypeRow(type = type, enabled = enabled, onToggle = { vm.toggle(type, !enabled) })
                }
            }
        }
    }
}

@Composable
private fun TypeRow(type: WorkoutType, enabled: Boolean, onToggle: () -> Unit) {
    val s = LocalStrings.current
    val isZh = s.isZh

    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(type.localizedName(isZh), style = MaterialTheme.typography.titleMedium)
                Text(if (enabled) s.applied else s.notApplied, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onToggle) {
                Text(if (enabled) s.cancelApply else s.apply)
            }
        }
    }
}