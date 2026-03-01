package au.edu.jcu.fittrackplus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
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

object Routes {
    const val HOME = "home"

    // returnToKey: 0 = none, 1 = schedule
    const val HOME_WITH_PRESET =
        "home?presetType={presetType}&presetMinutes={presetMinutes}&autostart={autostart}&returnToKey={returnToKey}"

    const val HOME_DEFAULT = "home?presetType=&presetMinutes=0&autostart=0&returnToKey=0"

    const val SETTINGS = "settings"

    const val SCHEDULE_LIST = "schedule/list"
    const val SCHEDULE_CREATE = "schedule/create"
    const val SCHEDULE_DETAIL = "schedule/detail/{id}"
    const val HISTORY = "history"

    const val SETTINGS_PROFILE = "settings/profile"
    const val SETTINGS_TYPES = "settings/types"
    const val SETTINGS_PREFS = "settings/prefs"

    fun scheduleDetail(id: Long): String = "schedule/detail/$id"

    fun homeWithPreset(typeName: String, minutes: Int): String =
        "home?presetType=$typeName&presetMinutes=$minutes&autostart=1&returnToKey=1"
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackNavGraph() {
    val s = LocalStrings.current
    val navController = rememberNavController()

    // ✅ 仅表示 “Home 正在 RUNNING/PAUSED”
    var workoutBlocking by remember { mutableStateOf(false) }

    // ✅ 0/1：是否来自 schedule 应用计划
    var returnToKey by rememberSaveable { mutableStateOf(0) }

    // HomeScreen 注册给 NavGraph 的 handler
    var pauseWorkout by remember { mutableStateOf<(() -> Unit)?>(null) }
    var resumeWorkout by remember { mutableStateOf<(() -> Unit)?>(null) }
    var discardWorkout by remember { mutableStateOf<(() -> Unit)?>(null) }

    // 离开确认弹窗
    var showLeaveDialog by remember { mutableStateOf(false) }
    var pendingTabRoute by remember { mutableStateOf<String?>(null) }

    // 当前目的地（用于：只在 Home 拦截）
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val selectedTabRoute = currentDestination.asBottomTabRoute()

    // ✅ 弹窗出现时暂停
    LaunchedEffect(showLeaveDialog) {
        if (showLeaveDialog) pauseWorkout?.invoke()
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = {
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
                TextButton(onClick = {
                    showLeaveDialog = false
                    pendingTabRoute = null
                    resumeWorkout?.invoke()
                }) { Text(if (s.isZh) "继续" else "Continue") }
            },
            dismissButton = {
                TextButton(onClick = {
                    discardWorkout?.invoke()

                    val targetTab = pendingTabRoute
                    showLeaveDialog = false
                    pendingTabRoute = null

                    val target = if (returnToKey == 1) Routes.SCHEDULE_LIST else targetTab

                    when (target) {
                        Routes.SETTINGS -> navController.navigate(Routes.SETTINGS) { launchSingleTop = true }

                        Routes.SCHEDULE_LIST -> {
                            val popped = navController.popBackStack(Routes.SCHEDULE_LIST, false)
                            if (!popped) navController.navigate(Routes.SCHEDULE_LIST) { launchSingleTop = true }
                        }

                        else -> navController.navigate(Routes.HOME_DEFAULT) { launchSingleTop = true }
                    }
                }) { Text(if (s.isZh) "退出" else "Exit") }
            }
        )
    }

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
                            // ✅ 只在 “Home 且 RUNNING/PAUSED” 时拦截底栏
                            if (selectedTabRoute == Routes.HOME && workoutBlocking) {
                                pendingTabRoute = route
                                showLeaveDialog = true
                                return@NavigationBarItem
                            }

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

                returnToKey = key

                HomeScreen(
                    onGoSchedule = { navController.navigate(Routes.SCHEDULE_LIST) },
                    onGoHistory = { navController.navigate(Routes.HISTORY) },

                    presetTypeName = typeName,
                    presetMinutes = minutes,
                    autoStart = autostart,

                    returnToRoute = if (key == 1) Routes.SCHEDULE_LIST else "",

                    // ✅ 关键：如果返回 schedule/list，把 snackbar 事件写到 schedule 的 savedStateHandle
                    onNavigateBack = { target ->
                        if (target == Routes.SCHEDULE_LIST) {
                            runCatching {
                                navController.getBackStackEntry(Routes.SCHEDULE_LIST)
                                    .savedStateHandle["snack"] = "SAVED"
                            }
                            navController.popBackStack(Routes.SCHEDULE_LIST, false)
                        } else {
                            val popped = navController.popBackStack(target, false)
                            if (!popped) navController.navigate(target) { launchSingleTop = true }
                        }
                    },

                    onWorkoutActiveChange = { active -> workoutBlocking = active },
                    onReturnToRouteChange = { /* no-op */ },

                    registerPauseHandler = { h -> pauseWorkout = h },
                    registerResumeHandler = { h -> resumeWorkout = h },
                    registerDiscardHandler = { h -> discardWorkout = h }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onGoProfile = { navController.navigate(Routes.SETTINGS_PROFILE) },
                    onGoCategory = { navController.navigate(Routes.SETTINGS_TYPES) },
                    onGoPreferences = { navController.navigate(Routes.SETTINGS_PREFS) }
                )
            }

            composable(Routes.SETTINGS_PROFILE) { ProfileSettingsScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.SETTINGS_TYPES) { WorkoutTypeManageScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.SETTINGS_PREFS) { PreferencesScreen(onBack = { navController.popBackStack() }) }

            // ✅ Schedule list：读取 savedStateHandle 的 snack 事件
            composable(Routes.SCHEDULE_LIST) { entry ->
                val snackKeyFlow = remember(entry) {
                    entry.savedStateHandle.getStateFlow("snack", "")
                }
                val snackKey by snackKeyFlow.collectAsState()

                ScheduleScreen(
                    onBack = { navController.popBackStack() },
                    onCreate = { navController.navigate(Routes.SCHEDULE_CREATE) },
                    onDetail = { id -> navController.navigate(Routes.scheduleDetail(id)) },
                    onApply = { type, mins ->
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
                ScheduleDetailScreen(planId = id, onBack = { navController.popBackStack() })
            }

            composable(Routes.HISTORY) {
                HistoryScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}