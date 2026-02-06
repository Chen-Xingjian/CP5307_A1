package au.edu.jcu.fittrackplus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import au.edu.jcu.fittrackplus.ui.history.HistoryScreen
import au.edu.jcu.fittrackplus.ui.home.HomeScreen
import au.edu.jcu.fittrackplus.ui.profile.ProfileScreen
import au.edu.jcu.fittrackplus.ui.records.RecordScreen
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleScreen
import au.edu.jcu.fittrackplus.ui.settings.SettingsScreen

/**
 * 全局路由定义
 */
object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"

    // Secondary screens (不出现在底部导航)
    const val RECORD = "record"
    const val HISTORY = "history"
    const val SCHEDULE = "schedule"
    const val PROFILE = "profile"
}

/**
 * 底部导航项：只保留两个
 */
private data class BottomItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Default.Home),
    BottomItem(Routes.SETTINGS, "Settings", Icons.Default.Settings)
)

/**
 * 判断当前页面是否显示底部导航
 * 仅 Home / Settings 显示
 */
private fun shouldShowBottomBar(destination: NavDestination?): Boolean {
    val route = destination?.route
    return route == Routes.HOME || route == Routes.SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = shouldShowBottomBar(currentDestination)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentDestination?.route) {
                            Routes.HOME -> "FitTrack+"
                            Routes.SETTINGS -> "Settings"
                            Routes.RECORD -> "Record Workout"
                            Routes.HISTORY -> "Workout History"
                            Routes.SCHEDULE -> "Schedule"
                            Routes.PROFILE -> "Profile"
                            else -> "FitTrack+"
                        }
                    )
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            /**
             * 主页面：Home
             * 通过回调跳转到二级功能页
             */
            composable(Routes.HOME) {
                HomeScreen(
                    onClickQuickStart = { navController.navigate(Routes.RECORD) },
                    onClickHistory = { navController.navigate(Routes.HISTORY) },
                    onClickSchedule = { navController.navigate(Routes.SCHEDULE) }
                )
            }

            /**
             * 主页面：Settings
             */
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onClickProfile = { navController.navigate(Routes.PROFILE) }
                )
            }

            /**
             * 二级页面（不在底部导航）
             */
            composable(Routes.RECORD) {
                RecordScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.HISTORY) {
                HistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SCHEDULE) {
                ScheduleScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}