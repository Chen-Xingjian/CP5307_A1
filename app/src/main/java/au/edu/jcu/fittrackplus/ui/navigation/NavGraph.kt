package au.edu.jcu.fittrackplus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*

import au.edu.jcu.fittrackplus.R
import au.edu.jcu.fittrackplus.ui.home.HomeScreen
import au.edu.jcu.fittrackplus.ui.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackNavGraph() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        BottomItem(Routes.HOME, "Home", Icons.Default.Home),
        BottomItem(Routes.SETTINGS, "Settings", Icons.Default.Settings)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val current = backStackEntry?.destination

                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
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
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
        }
    }
}