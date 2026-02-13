package au.edu.jcu.fittrackplus.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.edu.jcu.fittrackplus.ui.history.HistoryContent
import au.edu.jcu.fittrackplus.ui.record.RecordContent
import au.edu.jcu.fittrackplus.ui.schedule.ScheduleContent

private enum class HomeTab(val label: String) {
    RECORD("Record"),
    SCHEDULE("Schedule"),
    HISTORY("History")
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(HomeTab.RECORD) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Home", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            HomeTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            HomeTab.RECORD -> RecordContent()
            HomeTab.SCHEDULE -> ScheduleContent()
            HomeTab.HISTORY -> HistoryContent()
        }
    }
}