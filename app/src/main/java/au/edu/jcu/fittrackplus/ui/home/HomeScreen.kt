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

/**
 * Home screen for starting workouts.
 *
 * This screen supports two entry modes:
 * 1) Regular usage (manual selection + quick start).
 * 2) Plan-applied usage (preset type + preset duration, optionally auto-started).
 *
 * Important:
 * - UI styling is handled here, but business logic is delegated to [HomeViewModel].
 * - Navigation decisions are delegated via callbacks to the NavGraph (caller).
 */
@Composable
fun HomeScreen(
    /** Navigate to Schedule list. */
    onGoSchedule: () -> Unit,
    /** Navigate to History screen. */
    onGoHistory: () -> Unit,

    /** Preset workout type (WorkoutType.name) from plan-apply flow. */
    presetTypeName: String = "",
    /** Preset duration in minutes from plan-apply flow. */
    presetMinutes: Int = 0,
    /** If true, the preset plan should auto-start once when entering the screen. */
    autoStart: Boolean = false,

    /**
     * Non-empty indicates the workout was started from another screen (e.g., schedule).
     * When saved, Home should immediately navigate back to this route (without showing a Home snackbar).
     */
    returnToRoute: String = "",

    /**
     * Navigation helper used after a successful save (e.g., return back to schedule list).
     * The NavGraph controls the actual navigation stack behavior.
     */
    onNavigateBack: (String) -> Unit,

    /**
     * Callback for plan-applied workouts:
     * Only invoke when the plan-started workout is actually saved successfully.
     * Used by NavGraph to show "Saved to history" snackbar on the Schedule screen.
     */
    onPlanSaved: () -> Unit,

    /**
     * Reports whether the workout is actively running/paused.
     * Used by NavGraph to decide whether bottom navigation should be blocked by a leave-confirm dialog.
     */
    onWorkoutActiveChange: (Boolean) -> Unit,

    /**
     * Optional callback to report the current return-to route (kept for compatibility with NavGraph logic).
     */
    onReturnToRouteChange: (String) -> Unit,

    /**
     * Registers handlers for NavGraph's leave-confirm dialog:
     * - Pause the timer when the dialog shows
     * - Resume when user continues
     * - Discard when user exits without saving
     */
    registerPauseHandler: (((() -> Unit)) -> Unit),
    registerResumeHandler: (((() -> Unit)) -> Unit),
    registerDiscardHandler: (((() -> Unit)) -> Unit),

    /** Home screen ViewModel. */
    vm: HomeViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    // Collect UI state from ViewModel in a lifecycle-aware manner.
    val state by vm.ui.collectAsStateWithLifecycle()

    // Snackbar host for Home-specific messages (non-plan flows).
    val snack = remember { SnackbarHostState() }

    // UI-only state for dropdowns and dialogs.
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showCategoryHint by rememberSaveable { mutableStateOf(false) }

    /**
     * Ensures plan auto-start triggers at most once per unique preset tuple.
     * This prevents unintended re-trigger when the screen recomposes or returns to IDLE.
     */
    var autoStartConsumed by rememberSaveable(
        presetTypeName,
        presetMinutes,
        returnToRoute
    ) { mutableStateOf(false) }

    // Report return-to info upward (some NavGraph variants rely on this).
    LaunchedEffect(returnToRoute) {
        onReturnToRouteChange(returnToRoute)
    }

    // Report whether workout is active (RUNNING or PAUSED) for navigation guarding.
    LaunchedEffect(state.phase) {
        val active = state.phase == WorkoutPhase.RUNNING || state.phase == WorkoutPhase.PAUSED
        onWorkoutActiveChange(active)
    }

    // Register pause/resume/discard handlers for NavGraph's leave-confirm dialog.
    LaunchedEffect(Unit) {
        registerPauseHandler { vm.pauseForLeave() }
        registerResumeHandler { vm.resumeForLeave() }
        registerDiscardHandler { vm.discardAndReset() }
    }

    /**
     * Auto-start logic for plan-applied workouts.
     * Only runs once per preset. All preset application details are handled inside ViewModel.
     */
    LaunchedEffect(autoStart, presetTypeName, presetMinutes, autoStartConsumed) {
        if (!autoStart) return@LaunchedEffect
        if (autoStartConsumed) return@LaunchedEffect
        if (presetTypeName.isBlank() || presetMinutes <= 0) return@LaunchedEffect

        autoStartConsumed = true
        vm.startFromPlan(presetTypeName, presetMinutes)
    }

    /**
     * Save/toast handling:
     * - If a workout was started from schedule (returnToRoute is not blank) and saved:
     *   1) Notify NavGraph via [onPlanSaved] so Schedule can show snackbar
     *   2) Clear the toast key in VM
     *   3) Navigate back immediately (no Home snackbar)
     *
     * - Otherwise, show snackbar locally on Home and clear the toast key.
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

    // Exit confirmation dialog for STOPPED state (explicitly not saving).
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

    // Category hint dialog.
    if (showCategoryHint) {
        AlertDialog(
            onDismissRequest = { showCategoryHint = false },
            title = { Text(s.workoutTypesHintTitle) },
            text = { Text(s.workoutTypesHintBody) },
            confirmButton = { TextButton(onClick = { showCategoryHint = false }) { Text(s.ok) } }
        )
    }

    FitTrackScreen {
        // Top shortcuts are only visible in IDLE state.
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

            // Large round Quick Start button.
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

            // Setup card: category selector + target time inputs.
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

                    // Info icon (kept as a simple UI glyph, typically not localized).
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
            // Running/paused/stopped UI centered in the screen.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                FitTrackCard(modifier = Modifier.fillMaxWidth()) {
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
                            ) {
                                Text(if (state.isRunning) s.pause else s.start)
                            }

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

/**
 * Returns the seconds to show on screen.
 *
 * - If countdown is used, show remaining seconds.
 * - Otherwise, show elapsed seconds.
 */
private fun displaySeconds(state: HomeWorkoutUiState): Long =
    state.remainingSeconds ?: state.elapsedSeconds

/**
 * Formats seconds as HH:mm:ss.
 */
private fun formatHHmmss(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}