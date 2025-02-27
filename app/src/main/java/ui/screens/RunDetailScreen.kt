package com.example.runningtracker.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runningtracker.data.Run
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.*

@Composable
fun RunDetailsScreen(navController: NavController, runId: String, runViewModel: RunViewModel = viewModel()) {
    // Fetch the run by ID
    val run by runViewModel.allRuns.collectAsState(initial = emptyList()).let { runs ->
        runs.find { it.id.toString() == runId }
    }

    // Handle case where the run is not found
    if (run == null) {
        Text("Run not found!")
        return
    }

    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val mapProperties = remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }

    val context = LocalContext.current

    // Get the locations for the run (replace with actual locations from the run)
    val runLocations = run.locations.map { LatLng(it.latitude, it.longitude) }

    // Column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Run Details", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Run Date: ${run.timeInMillis}")
        Text(text = "Distance: %.2f miles".format(run.distanceInMeters / 1609.34f))
        Text(text = "Avg Speed: %.2f mph".format(run.avgSpeed * 0.621371))

        Spacer(modifier = Modifier.height(16.dp))

        // Google Map composable for showing the route
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            cameraPositionState = cameraPositionState,
            properties = mapProperties.value
        ) {
            // Add markers for each location in the run
            for (location in runLocations) {
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Run Point"
                )
            }

            // Draw polyline to show the route
            PolylineOptions().apply {
                addAll(runLocations)
                color(0xFF0000FF.toInt())
                width(5f)
            }.let {
                PolylineOptions.addPolyline(it)
            }
        }
    }
}
