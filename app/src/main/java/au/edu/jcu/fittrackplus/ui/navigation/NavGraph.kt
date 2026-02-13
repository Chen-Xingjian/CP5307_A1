package au.edu.jcu.fittrackplus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import au.edu.jcu.fittrackplus.ui.home.HomeScreen
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleCreateScreen
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleDetailScreen
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleScreen
import au.edu.jcu.fittrackplus.ui.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"

    const val SCHEDULE_LIST = "schedule/list"
    const val SCHEDULE_CREATE = "schedule/create"
    const val SCHEDULE_DETAIL = "schedule/detail/{id}"

    const val HISTORY = "history" // 你后面接真实 HistoryScreen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackNavGraph() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        Triple(Routes.HOME, "Home", Icons.Default.Home),
        Triple(Routes.SETTINGS, "Setting", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val current = backStackEntry?.destination
                bottomItems.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
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
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onGoSchedule = { navController.navigate(Routes.SCHEDULE_LIST) },
                    onGoHistory = { navController.navigate(Routes.HISTORY) }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }

            composable(Routes.SCHEDULE_LIST) {
                ScheduleScreen(
                    onBack = { navController.popBackStack() },
                    onCreate = { navController.navigate(Routes.SCHEDULE_CREATE) },
                    onItemClick = { id -> navController.navigate("schedule/detail/$id") }
                )
            }

            composable(Routes.SCHEDULE_CREATE) {
                ScheduleCreateScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SCHEDULE_DETAIL) { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 0L
                ScheduleDetailScreen(planId = id, onBack = { navController.popBackStack() })
            }

            // 占位（你后续替换为真正 HistoryScreen）
            composable(Routes.HISTORY) {
                Text("History page (to be implemented)")
            }
        }
    }
}