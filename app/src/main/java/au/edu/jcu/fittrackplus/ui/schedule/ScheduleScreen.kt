package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
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

    // ✅ 新增：NavGraph 传入的 snack 事件（例如 "SAVED"）
    snackKey: String = "",
    onSnackConsumed: () -> Unit = {},

    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh
    val plans by vm.plans.collectAsState(initial = emptyList())

    // ✅ Schedule 页自己的 SnackbarHost
    val snack = remember { SnackbarHostState() }

    // ✅ 消费事件：让 “Saved to history” 出现在 Schedule 底部
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
                    IconButton(onClick = onCreate) {
                        Icon(Icons.Default.Add, contentDescription = s.new)
                    }
                }
            )
        },
        // ✅ 底部 snackbar
        snackbarHost = { SnackbarHost(hostState = snack) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = plans, key = { it.id }) { plan ->
                PlanRow(
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
private fun PlanRow(
    plan: WorkoutPlan,
    isZh: Boolean,
    onDetail: () -> Unit,
    onDelete: () -> Unit,
    onApply: () -> Unit
) {
    val s = LocalStrings.current

    // plan.category 存的是 WorkoutType.name（例如 RUNNING）
    val type = WorkoutType.fromName(plan.category)
    val typeLabel = type.localizedName(isZh)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plan.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$typeLabel • ${plan.durationMinutes} min • ${plan.estimatedCalories} kcal",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onDetail) {
                    Icon(Icons.Default.Info, contentDescription = s.detail)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = s.delete)
                }
                IconButton(onClick = onApply) {
                    Icon(Icons.Default.PlayArrow, contentDescription = s.apply)
                }
            }
        }
    }
}