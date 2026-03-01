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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.ui.components.ClickableOutlinedField
import au.edu.jcu.fittrackplus.ui.components.FitTrackCard
import au.edu.jcu.fittrackplus.ui.components.FitTrackScreen
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

    // ✅ NEW: only called when a plan-started workout is actually saved
    onPlanSaved: () -> Unit,

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

    var autoStartConsumed by rememberSaveable(
        presetTypeName,
        presetMinutes,
        returnToRoute
    ) { mutableStateOf(false) }

    LaunchedEffect(returnToRoute) {
        onReturnToRouteChange(returnToRoute)
    }

    LaunchedEffect(state.phase) {
        val active = state.phase == WorkoutPhase.RUNNING || state.phase == WorkoutPhase.PAUSED
        onWorkoutActiveChange(active)
    }

    LaunchedEffect(Unit) {
        registerPauseHandler { vm.pauseForLeave() }
        registerResumeHandler { vm.resumeForLeave() }
        registerDiscardHandler { vm.discardAndReset() }
    }

    LaunchedEffect(autoStart, presetTypeName, presetMinutes, autoStartConsumed) {
        if (!autoStart) return@LaunchedEffect
        if (autoStartConsumed) return@LaunchedEffect
        if (presetTypeName.isBlank() || presetMinutes <= 0) return@LaunchedEffect

        autoStartConsumed = true
        vm.startFromPlan(presetTypeName, presetMinutes)
    }

    /**
     * ✅ FIX:
     * - If coming from schedule (returnToRoute != ""), and saved:
     *   1) fire onPlanSaved() to let NavGraph show snackbar in Schedule
     *   2) clear toast
     *   3) immediately navigate back (no snackbar on Home)
     */
    LaunchedEffect(state.lastSavedMessage) {
        state.lastSavedMessage?.let { key ->
            if (key == "SAVED" && returnToRoute.isNotBlank()) {
                onPlanSaved()
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

    FitTrackScreen {
        // 顶部快捷入口：只在 IDLE 显示
        if (state.isIdle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onGoSchedule) { Text(s.schedule) }
                TextButton(onClick = onGoHistory) { Text(s.history) }
            }
        }

        if (state.isIdle) {
            Spacer(Modifier.height(8.dp))

            // Quick Start button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { vm.startQuick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = s.quickStart,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            FitTrackCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        ClickableOutlinedField(
                            value = state.selectedCategory.localizedName(isZh),
                            label = s.category,
                            onClick = { categoryExpanded = true }
                        )

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            state.categoryOptions.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.localizedName(isZh)) },
                                    onClick = {
                                        vm.onCategoryChange(t)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "ⓘ",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showCategoryHint = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
            }

            Spacer(Modifier.height(8.dp))
            SnackbarHost(hostState = snack)
        } else {
            // Workout running UI (center)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                FitTrackCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.selectedCategory.localizedName(isZh),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "${s.time}: ${formatHHmmss(displaySeconds(state))}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    if (state.phase == WorkoutPhase.RUNNING || state.phase == WorkoutPhase.PAUSED) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { if (state.isRunning) vm.pause() else vm.resume() },
                                enabled = state.canControl,
                                modifier = Modifier.weight(1f)
                            ) { Text(if (state.isRunning) s.pause else s.start) }

                            Button(
                                onClick = vm::stop,
                                enabled = state.canControl,
                                modifier = Modifier.weight(1f)
                            ) { Text(s.stop) }
                        }
                    }

                    if (state.isStopped) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = vm::saveAndReset,
                                modifier = Modifier.weight(1f)
                            ) { Text(s.save) }

                            Button(
                                onClick = { showExitDialog = true },
                                modifier = Modifier.weight(1f)
                            ) { Text(s.exit) }
                        }
                    }
                }
            }

            SnackbarHost(hostState = snack)
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