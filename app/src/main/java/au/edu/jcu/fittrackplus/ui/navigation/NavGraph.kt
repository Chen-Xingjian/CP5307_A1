package au.edu.jcu.fittrackplus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import au.edu.jcu.fittrackplus.ui.history.HistoryScreen
import au.edu.jcu.fittrackplus.ui.home.HomeScreen
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleCreateScreen
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleDetailScreen
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleScreen
import au.edu.jcu.fittrackplus.ui.settings.PreferencesScreen
import au.edu.jcu.fittrackplus.ui.settings.ProfileSettingsScreen
import au.edu.jcu.fittrackplus.ui.settings.SettingsScreen
import au.edu.jcu.fittrackplus.ui.settings.WorkoutTypeManageScreen

/**
 * Centralized route definitions for the app's navigation graph.
 *
 * Notes:
 * - Home supports optional query parameters used when starting a workout from a plan.
 * - returnToKey: 0 = no special return destination, 1 = return to Schedule list after saving.
 */
object Routes {
    const val HOME = "home"

    /**
     * Home route template with query parameters.
     *
     * Query parameters:
     * - presetType: WorkoutType.name to be applied before auto-start
     * - presetMinutes: countdown duration in minutes
     * - autostart: 1 = start automatically
     * - returnToKey: 1 = started from Schedule list
     */
    const val HOME_WITH_PRESET =
        "home?presetType={presetType}&presetMinutes={presetMinutes}&autostart={autostart}&returnToKey={returnToKey}"

    /** Default Home route with empty preset and no auto-start. */
    const val HOME_DEFAULT = "home?presetType=&presetMinutes=0&autostart=0&returnToKey=0"

    const val SETTINGS = "settings"

    const val SCHEDULE_LIST = "schedule/list"
    const val SCHEDULE_CREATE = "schedule/create"
    const val SCHEDULE_DETAIL = "schedule/detail/{id}"
    const val HISTORY = "history"

    const val SETTINGS_PROFILE = "settings/profile"
    const val SETTINGS_TYPES = "settings/types"
    const val SETTINGS_PREFS = "settings/prefs"

    /** Builds a schedule detail route for the given plan id. */
    fun scheduleDetail(id: Long): String = "schedule/detail/$id"

    /**
     * Builds a Home route that auto-starts a workout using the selected plan.
     *
     * - autostart=1 triggers automatic start in HomeScreen
     * - returnToKey=1 marks that the workout originated from Schedule
     */
    fun homeWithPreset(typeName: String, minutes: Int): String =
        "home?presetType=$typeName&presetMinutes=$minutes&autostart=1&returnToKey=1"
}

/**
 * Maps nested routes back to the bottom tab they belong to.
 *
 * This ensures the bottom tab highlight remains correct when navigating into sub-pages.
 */
private fun NavDestination?.asBottomTabRoute(): String {
    val route = this?.route.orEmpty()
    return when {
        route == Routes.SETTINGS ||
                route == Routes.SETTINGS_PROFILE ||
                route == Routes.SETTINGS_TYPES ||
                route == Routes.SETTINGS_PREFS -> Routes.SETTINGS

        route.startsWith(Routes.HOME) ||
                route == Routes.SCHEDULE_LIST ||
                route == Routes.SCHEDULE_CREATE ||
                route.startsWith("schedule/detail/") ||
                route == Routes.SCHEDULE_DETAIL ||
                route == Routes.HISTORY -> Routes.HOME

        else -> Routes.HOME
    }
}

/**
 * Root navigation graph with a bottom navigation bar.
 *
 * Key behaviors:
 * - When a workout is RUNNING/PAUSED on Home, switching tabs is intercepted by a leave-confirm dialog.
 * - When leaving during an active workout, the workout is paused while the dialog is visible.
 * - If a plan-started workout is saved, a snackbar event is routed back to Schedule via savedStateHandle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackNavGraph() {
    val s = LocalStrings.current
    val navController = rememberNavController()

    /**
     * True only when Home is actively RUNNING/PAUSED.
     * Used to decide whether bottom tab clicks should be intercepted.
     */
    var workoutBlocking by remember { mutableStateOf(false) }

    /**
     * Tracks the origin of the current workout session.
     * 0 = normal Home start, 1 = started from Schedule list.
     */
    var returnToKey by rememberSaveable { mutableStateOf(0) }

    /**
     * Handlers registered by HomeScreen so NavGraph can pause/resume/discard
     * when showing the leave-confirm dialog.
     */
    var pauseWorkout by remember { mutableStateOf<(() -> Unit)?>(null) }
    var resumeWorkout by remember { mutableStateOf<(() -> Unit)?>(null) }
    var discardWorkout by remember { mutableStateOf<(() -> Unit)?>(null) }

    /** Leave-confirm dialog visibility. */
    var showLeaveDialog by remember { mutableStateOf(false) }

    /** The tab route the user attempted to navigate to while blocked. */
    var pendingTabRoute by remember { mutableStateOf<String?>(null) }

    /** Current destination used for tab highlighting and interception logic. */
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val selectedTabRoute = currentDestination.asBottomTabRoute()

    /**
     * Pause the workout when the leave-confirm dialog becomes visible.
     * This prevents the timer from continuing while the user decides.
     */
    LaunchedEffect(showLeaveDialog) {
        if (showLeaveDialog) pauseWorkout?.invoke()
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = {
                // Treat dismiss as "Continue".
                showLeaveDialog = false
                pendingTabRoute = null
                resumeWorkout?.invoke()
            },
            title = { Text(if (s.isZh) "离开页面？" else "Leave?") },
            text = {
                Text(
                    if (s.isZh) "现在离开将终止运动，并且不会保存。"
                    else "Leaving now will stop the workout and it will NOT be saved."
                )
            },
            confirmButton = {
                // Continue: resume the workout and stay on the current screen.
                TextButton(onClick = {
                    showLeaveDialog = false
                    pendingTabRoute = null
                    resumeWorkout?.invoke()
                }) { Text(if (s.isZh) "继续" else "Continue") }
            },
            dismissButton = {
                // Exit: discard the workout (no save) and navigate to the intended tab.
                TextButton(onClick = {
                    // Stop workout without saving.
                    discardWorkout?.invoke()

                    val targetTab = pendingTabRoute

                    // Close dialog and clear pending state.
                    showLeaveDialog = false
                    pendingTabRoute = null

                    // Clear blocking flags to avoid getting stuck in an intercepted state.
                    workoutBlocking = false
                    returnToKey = 0

                    // Respect the user's explicit tab choice first.
                    when (targetTab) {
                        Routes.HOME -> {
                            navController.navigate(Routes.HOME_DEFAULT) { launchSingleTop = true }
                            return@TextButton
                        }

                        Routes.SETTINGS -> {
                            navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                            return@TextButton
                        }
                    }

                    // Fallback: navigate to Home by default.
                    navController.navigate(Routes.HOME_DEFAULT) { launchSingleTop = true }
                }) { Text(if (s.isZh) "退出" else "Exit") }
            }
        )
    }

    /** Bottom navigation items (two tabs). */
    val bottomItems = listOf(
        Triple(Routes.HOME, s.home, Icons.Default.Home),
        Triple(Routes.SETTINGS, s.setting, Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = selectedTabRoute == route,
                        onClick = {
                            /**
                             * Intercept tab clicks only when:
                             * - User is currently on Home tab, and
                             * - Workout is RUNNING/PAUSED (blocking is true).
                             */
                            if (selectedTabRoute == Routes.HOME && workoutBlocking) {
                                pendingTabRoute = route
                                showLeaveDialog = true
                                return@NavigationBarItem
                            }

                            // Normal navigation when not blocked.
                            if (route == Routes.HOME) {
                                navController.navigate(Routes.HOME_DEFAULT) { launchSingleTop = true }
                            } else {
                                navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_DEFAULT,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Routes.HOME_WITH_PRESET,
                arguments = listOf(
                    navArgument("presetType") { type = NavType.StringType; defaultValue = "" },
                    navArgument("presetMinutes") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("autostart") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("returnToKey") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { entry ->
                val typeName = entry.arguments?.getString("presetType").orEmpty()
                val minutes = entry.arguments?.getInt("presetMinutes") ?: 0
                val autostart = (entry.arguments?.getInt("autostart") ?: 0) == 1
                val key = entry.arguments?.getInt("returnToKey") ?: 0

                // Track whether this workout originated from Schedule.
                returnToKey = key

                HomeScreen(
                    onGoSchedule = { navController.navigate(Routes.SCHEDULE_LIST) },
                    onGoHistory = { navController.navigate(Routes.HISTORY) },

                    presetTypeName = typeName,
                    presetMinutes = minutes,
                    autoStart = autostart,

                    // If started from Schedule, Home will navigate back to Schedule after saving.
                    returnToRoute = if (key == 1) Routes.SCHEDULE_LIST else "",

                    /**
                     * Navigation callback used by HomeScreen to return to a specific route.
                     * This callback does not emit snackbar events.
                     */
                    onNavigateBack = { target ->
                        val popped = navController.popBackStack(target, false)
                        if (!popped) navController.navigate(target) { launchSingleTop = true }
                    },

                    /**
                     * Called only when a plan-started workout is saved successfully.
                     * The snackbar is shown on Schedule via savedStateHandle.
                     */
                    onPlanSaved = {
                        runCatching {
                            navController.getBackStackEntry(Routes.SCHEDULE_LIST)
                                .savedStateHandle["snack"] = "SAVED"
                        }
                    },

                    // Block tab switching only when workout is actively running/paused.
                    onWorkoutActiveChange = { active -> workoutBlocking = active },
                    onReturnToRouteChange = { /* Intentionally unused here. */ },

                    // Register pause/resume/discard handlers for the leave-confirm dialog flow.
                    registerPauseHandler = { h -> pauseWorkout = h },
                    registerResumeHandler = { h -> resumeWorkout = h },
                    registerDiscardHandler = { h -> discardWorkout = h }
                )
            }

            // Settings root
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onGoProfile = { navController.navigate(Routes.SETTINGS_PROFILE) },
                    onGoCategory = { navController.navigate(Routes.SETTINGS_TYPES) },
                    onGoPreferences = { navController.navigate(Routes.SETTINGS_PREFS) }
                )
            }

            // Settings sub-pages
            composable(Routes.SETTINGS_PROFILE) {
                ProfileSettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS_TYPES) {
                WorkoutTypeManageScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS_PREFS) {
                PreferencesScreen(onBack = { navController.popBackStack() })
            }

            // Schedule list: reads snack events from savedStateHandle
            composable(Routes.SCHEDULE_LIST) { entry ->
                val snackKeyFlow = remember(entry) { entry.savedStateHandle.getStateFlow("snack", "") }
                val snackKey by snackKeyFlow.collectAsState()

                ScheduleScreen(
                    onBack = { navController.popBackStack() },
                    onCreate = { navController.navigate(Routes.SCHEDULE_CREATE) },
                    onDetail = { id -> navController.navigate(Routes.scheduleDetail(id)) },
                    onApply = { type, mins ->
                        // Clear any stale snackbar key before starting a new plan workout.
                        entry.savedStateHandle["snack"] = ""
                        navController.navigate(Routes.homeWithPreset(type, mins)) { launchSingleTop = true }
                    },
                    snackKey = snackKey,
                    onSnackConsumed = { entry.savedStateHandle["snack"] = "" }
                )
            }

            composable(Routes.SCHEDULE_CREATE) {
                ScheduleCreateScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = Routes.SCHEDULE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStack ->
                val id = backStack.arguments?.getLong("id") ?: 0L
                ScheduleDetailScreen(
                    planId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.HISTORY) {
                HistoryScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}