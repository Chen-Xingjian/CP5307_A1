package au.edu.jcu.fittrackplus.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onClickQuickStart: () -> Unit,
    onClickHistory: () -> Unit,
    onClickSchedule: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to FitTrack+")
        Button(onClick = onClickQuickStart) { Text("Quick Start Workout") }
        Button(onClick = onClickHistory) { Text("View History") }
        Button(onClick = onClickSchedule) { Text("Schedule Workout") }
    }
}