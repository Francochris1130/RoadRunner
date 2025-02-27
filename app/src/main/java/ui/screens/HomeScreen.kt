package com.example.runningtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runningtracker.data.Run
import com.example.runningtracker.data.formatDateInEST
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController, runViewModel: RunViewModel = viewModel()) {
    // Collect all runs from the ViewModel
    val runs by runViewModel.allRuns.collectAsState(initial = emptyList())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Running Tracker", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("run") }) {
            Text(text = "Start a Run")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Run History", style = MaterialTheme.typography.headlineSmall)

        // Display the list of runs
        RunHistoryList(runs = runs, navController = navController)
    }
}

@Composable
fun RunHistoryList(runs: List<Run>, navController: NavController) {
    // Sort runs by the most recent first (descending order)
    val sortedRuns = runs.sortedByDescending { it.timeInMillis }

    LazyColumn {
        items(sortedRuns) { run ->
            // Format the date for display
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val estTimeZone = TimeZone.getTimeZone("America/New_York") // EST timezone
            dateFormat.timeZone = estTimeZone
            val runDate = dateFormat.format(Date(run.timeInMillis))
            val formattedDate = formatDateInEST(run.date)
            Text(text = formattedDate)

            // Display each run as a Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        // Navigate to the detailed run screen, passing the run data
                        navController.navigate("runDetail/${run.id}")
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Run on: $runDate",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Distance: %.2f miles".format(run.distanceInMeters / 1609.34f), // Convert meters to miles
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Avg Speed: %.2f mph".format(run.avgSpeed * 0.621371), // Convert km/h to mph
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
