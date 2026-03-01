package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.fittrackplus.domain.model.WorkoutPlan
import au.edu.jcu.fittrackplus.domain.model.WorkoutType
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onDetail: (Long) -> Unit,
    onApply: (presetTypeName: String, presetMinutes: Int) -> Unit,

    // snack event from NavGraph (e.g. "SAVED")
    snackKey: String = "",
    onSnackConsumed: () -> Unit = {},

    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh
    val plans by vm.plans.collectAsState(initial = emptyList())

    val snack = remember { SnackbarHostState() }

    LaunchedEffect(snackKey) {
        if (snackKey == "SAVED") {
            snack.showSnackbar(s.savedToHistory)
            onSnackConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.scheduleTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } },
                actions = {
                    FilledIconButton(
                        onClick = onCreate,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = s.new)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snack) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = plans, key = { it.id }) { plan ->
                PlanCard(
                    plan = plan,
                    isZh = isZh,
                    onDetail = { onDetail(plan.id) },
                    onDelete = { vm.deletePlan(plan.id) },
                    onApply = { onApply(plan.category, plan.durationMinutes) }
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: WorkoutPlan,
    isZh: Boolean,
    onDetail: () -> Unit,
    onDelete: () -> Unit,
    onApply: () -> Unit
) {
    val s = LocalStrings.current

    val type = WorkoutType.fromName(plan.category)
    val typeLabel = type.localizedName(isZh)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: main info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Secondary line: type + duration + kcal
                Text(
                    text = "$typeLabel • ${plan.durationMinutes} min • ${plan.estimatedCalories} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Optional note preview (small, only when not blank)
                if (plan.note.isNotBlank()) {
                    Text(
                        text = plan.note,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }

            // Right: actions (3 round buttons)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoundActionButton(
                    onClick = onDetail,
                    contentDescription = s.detail,
                ) { Icon(Icons.Default.Info, contentDescription = null) }

                RoundActionButton(
                    onClick = onDelete,
                    contentDescription = s.delete,
                ) { Icon(Icons.Default.Delete, contentDescription = null) }

                RoundActionButton(
                    onClick = onApply,
                    contentDescription = s.apply,
                ) { Icon(Icons.Default.PlayArrow, contentDescription = null) }
            }
        }
    }
}

@Composable
private fun RoundActionButton(
    onClick: () -> Unit,
    contentDescription: String,
    content: @Composable () -> Unit
) {
    // A subtle filled background using secondaryContainer so it looks like an app UI
    FilledIconButton(
        onClick = onClick,
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}