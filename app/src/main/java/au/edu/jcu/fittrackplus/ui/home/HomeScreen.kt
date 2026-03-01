package au.edu.jcu.fittrackplus.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

@Composable
fun HomeScreen(
    onGoSchedule: () -> Unit,
    onGoHistory: () -> Unit,

    presetTypeName: String = "",
    presetMinutes: Int = 0,
    autoStart: Boolean = false,

    returnToRoute: String = "",

    onNavigateBack: (String) -> Unit,

    onWorkoutActiveChange: (Boolean) -> Unit,
    onReturnToRouteChange: (String) -> Unit,

    registerPauseHandler: (((() -> Unit)) -> Unit),
    registerResumeHandler: (((() -> Unit)) -> Unit),
    registerDiscardHandler: (((() -> Unit)) -> Unit),

    vm: HomeViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    val state by vm.ui.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }

    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showCategoryHint by rememberSaveable { mutableStateOf(false) }

    // ✅ 计划 autostart 只允许触发一次
    var autoStartConsumed by rememberSaveable(
        presetTypeName,
        presetMinutes,
        returnToRoute
    ) { mutableStateOf(false) }

    LaunchedEffect(returnToRoute) {
        onReturnToRouteChange(returnToRoute)
    }

    // ✅ 只在 RUNNING/PAUSED 才算“运动中”
    LaunchedEffect(state.phase) {
        val active = state.phase == WorkoutPhase.RUNNING || state.phase == WorkoutPhase.PAUSED
        onWorkoutActiveChange(active)
    }

    // ✅ 注册 pause / resume / discard handler 给 NavGraph
    LaunchedEffect(Unit) {
        registerPauseHandler { vm.pauseForLeave() }
        registerResumeHandler { vm.resumeForLeave() }
        registerDiscardHandler { vm.discardAndReset() }
    }

    // ✅ preset 自动开始（只触发一次）
    LaunchedEffect(autoStart, presetTypeName, presetMinutes, autoStartConsumed) {
        if (!autoStart) return@LaunchedEffect
        if (autoStartConsumed) return@LaunchedEffect
        if (presetTypeName.isBlank() || presetMinutes <= 0) return@LaunchedEffect

        autoStartConsumed = true
        vm.startFromPlan(presetTypeName, presetMinutes)
    }

    /**
     * ✅ 关键修复：
     * - 来自 schedule（returnToRoute != ""）的保存：不要在 Home 弹 snackbar
     * - 立刻回到 schedule，并清掉 toast key，避免回到 IDLE 又触发逻辑
     */
    LaunchedEffect(state.lastSavedMessage) {
        state.lastSavedMessage?.let { key ->
            if (key == "SAVED" && returnToRoute.isNotBlank()) {
                vm.clearToast()
                onNavigateBack(returnToRoute)
                return@LaunchedEffect
            }

            val msg = when (key) {
                "SAVED" -> s.savedToHistory
                "TIME_UP" -> s.timeUp
                else -> key
            }
            snack.showSnackbar(msg)
            vm.clearToast()
        }
    }

    // ✅ Exit dialog（STOPPED 后点击 Exit）
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(s.exitWorkoutTitle) },
            text = { Text(s.exitWorkoutBody) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    vm.saveAndReset()
                }) { Text(s.save) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    vm.discardAndReset()
                    if (returnToRoute.isNotBlank()) onNavigateBack(returnToRoute)
                }) { Text(s.exit) }
            }
        )
    }

    if (showCategoryHint) {
        AlertDialog(
            onDismissRequest = { showCategoryHint = false },
            title = { Text(s.workoutTypesHintTitle) },
            text = { Text(s.workoutTypesHintBody) },
            confirmButton = { TextButton(onClick = { showCategoryHint = false }) { Text(s.ok) } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // ✅ 运动中隐藏顶部 Schedule/History
        if (state.isIdle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onGoSchedule) { Text(s.schedule) }
                TextButton(onClick = onGoHistory) { Text(s.history) }
            }
            Spacer(Modifier.height(18.dp))
        } else {
            Spacer(Modifier.height(6.dp))
        }

        if (state.isIdle) {
            // ===== IDLE =====
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(18.dp))

                Card(
                    modifier = Modifier.size(200.dp).clip(CircleShape)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().clickable { vm.startQuick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = s.quickStart,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // category + hint
                Row(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.selectedCategory.localizedName(isZh),
                            onValueChange = {},
                            readOnly = true,
                            enabled = true, // ✅ 关键：不要 disabled，否则会变灰框
                            label = { Text(s.category) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(Modifier.matchParentSize().clickable { categoryExpanded = true })
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            state.categoryOptions.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.localizedName(isZh)) },
                                    onClick = { vm.onCategoryChange(t); categoryExpanded = false }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ⓘ",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showCategoryHint = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Hour / Min input
                Row(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = state.targetHourText,
                        onValueChange = vm::onTargetHourChange,
                        label = { Text(s.hour) },
                        placeholder = { Text("0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.targetMinuteText,
                        onValueChange = vm::onTargetMinuteChange,
                        label = { Text(s.minute) },
                        placeholder = { Text("00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(24.dp))
                SnackbarHost(hostState = snack)
            }
        } else {
            // ===== RUNNING/PAUSED/STOPPED =====
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(26.dp))

                Text(
                    text = state.selectedCategory.localizedName(isZh),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "${s.time}: ${formatHHmmss(displaySeconds(state))}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(28.dp))

                if (state.phase == WorkoutPhase.RUNNING || state.phase == WorkoutPhase.PAUSED) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { if (state.isRunning) vm.pause() else vm.resume() },
                            enabled = state.canControl
                        ) { Text(if (state.isRunning) s.pause else s.start) }

                        Button(
                            onClick = vm::stop,
                            enabled = state.canControl
                        ) { Text(s.stop) }
                    }
                }

                if (state.isStopped) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = vm::saveAndReset) { Text(s.save) }
                        Button(onClick = { showExitDialog = true }) { Text(s.exit) }
                    }
                }

                Spacer(Modifier.height(24.dp))
                SnackbarHost(hostState = snack)
            }
        }
    }
}

private fun displaySeconds(state: HomeWorkoutUiState): Long =
    state.remainingSeconds ?: state.elapsedSeconds

private fun formatHHmmss(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}