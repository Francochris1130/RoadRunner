package com.example.runningtracker.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.runningtracker.data.Run
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun StatisticsScreen(navController: NavHostController, runViewModel: RunViewModel = viewModel()) {
    val runs by runViewModel.allRuns.collectAsState(initial = emptyList())

    // Convert total distance from meters to miles (1 km = 0.621371 miles)
    val totalDistanceInMiles = runs.sumOf { it.distanceInMeters } / 1609.34f // Convert meters to miles
    val totalTimeInMinutes = runs.sumOf { it.timeInMillis } / 60000f // Convert millis to minutes

    // Calculate avg speed in miles per hour (1 km/h = 0.621371 mph)
    val avgSpeed = if (runs.isNotEmpty()) totalDistanceInMiles / (totalTimeInMinutes / 60f) else 0f // mph

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Run Statistics", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Display statistics with conversions
        Text(text = "Total Runs: ${runs.size}")
        Text(text = "Total Distance: %.2f miles".format(totalDistanceInMiles)) // Display in miles
        Text(text = "Avg Speed: %.2f mph".format(avgSpeed)) // Display in mph

        Spacer(modifier = Modifier.height(16.dp))

        // Display Chart
        RunStatisticsChart(runs)
    }
}

@Composable
fun RunStatisticsChart(runs: List<Run>) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx: Context ->
            LineChart(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    600
                )

                // Configure chart appearance
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false

                // Convert distance to miles for the chart (1 km = 0.621371 miles)
                val entries = runs.mapIndexed { index, run ->
                    Entry(index.toFloat(), run.distanceInMeters / 1609.34f) // Convert meters to miles
                }

                val dataSet = LineDataSet(entries, "Distance per Run").apply {
                    color = ColorTemplate.MATERIAL_COLORS[0]
                    valueTextColor = Color.WHITE
                    setCircleColor(Color.BLUE)
                    lineWidth = 2f
                }

                data = LineData(dataSet)
                invalidate()
            }
        }
    )
}
