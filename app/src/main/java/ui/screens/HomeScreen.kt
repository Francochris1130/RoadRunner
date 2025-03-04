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
        Text(text = "Welcome to RoadRunners!", style = MaterialTheme.typography.headlineMedium)

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
    val groupedRuns = runs
        .sortedByDescending { it.date }
        .groupBy { formatDateInEST(it.date) } // Group by formatted date (day)

    LazyColumn {
        groupedRuns.forEach { (date, runsOnDate) ->
            // Date Header
            item {
                Text(
                    text = date,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }

            // List of runs for that date
            items(runsOnDate) { run ->
                RunCard(run, navController)
            }
        }
    }
}

@Composable
fun RunCard(run: Run, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { navController.navigate("runDetail/${run.id}") },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Run ${run.id}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Distance: %.2f miles".format(run.distanceInMeters / 1609.34f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Avg Speed: %.2f mph".format(run.avgSpeed * 0.621371f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
