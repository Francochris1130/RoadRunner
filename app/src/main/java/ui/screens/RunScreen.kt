package com.example.runningtracker.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlin.random.Random
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.Marker
import com.google.android.gms.maps.model.MarkerOptions


@Composable
fun RunScreen(navController: NavController) {
    // Initialize ViewModel
    val runViewModel: RunViewModel = viewModel()

    var isTracking by remember { mutableStateOf(false) }
    var distance by remember { mutableStateOf(0) }
    var time by remember { mutableStateOf(0L) }
    var speed by remember { mutableStateOf(0f) }
    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    // Create lists to store data for charting
    val distanceList = remember { mutableStateListOf<Float>() }
    val timeList = remember { mutableStateListOf<Float>() }

    // Map state
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }

    // Context to get the FusedLocationClient
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Function to request location updates
    fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted, return
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
            }
        }
    }

    // Column layout for the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Run Tracking", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Start/Stop buttons for tracking
        Button(onClick = {
            if (isTracking) {
                runViewModel.stopRun()
                isTracking = false
            } else {
                runViewModel.startRun(distance, time, speed)
                isTracking = true
            }
        }) {
            Text(text = if (isTracking) "Stop Run" else "Start Run")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Convert distance from meters to miles (1 mile = 1609.34 meters)
        val distanceInMiles = distance / 1609.34f

        // Convert speed from meters per second to miles per hour (1 m/s = 2.23694 mph)
        val speedInMph = speed * 2.23694f

        // Display tracking information
        Text(text = "Distance: ${"%.2f".format(distanceInMiles)} miles")
        Text(text = "Time: $time seconds")
        Text(text = "Avg Speed: ${"%.2f".format(speedInMph)} mph")

        // Simulating tracking (for demo purposes)
        LaunchedEffect(isTracking) {
            while (isTracking) {
                delay(1000) // 1-second interval
                distance += Random.nextInt(10, 50) // Simulate distance
                time += 1 // Increase time by 1 second
                speed = if (time > 0) distance / time.toFloat() else 0f // Avoid division by zero

                // Update currentRun in ViewModel
                runViewModel.updateRun(distance, time, speed)

                // Add data to lists for charting
                distanceList.add(distance.toFloat())
                timeList.add(time.toFloat())

                updateLocation() // Update location every second
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Map composable for live tracking
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            cameraPositionState = cameraPositionState,
            properties = mapProperties
        ) {
            // Use rememberMarkerState instead of MarkerState
            val markerState = rememberMarkerState(position = currentLocation)
            Marker(
                state = markerState,
                title = "Current Location"
            )
        }

        // Add the statistics chart here
        RunStatisticsChart(distanceList, timeList)
    }
}



@Composable
fun RunStatisticsChart(distanceList: List<Float>, timeList: List<Float>) {
    // This is a placeholder function for charting the distance and time.
    // You could replace this with an actual chart library, e.g., ComposeCharts or MPAndroidChart wrapped in AndroidView
    // Here we're just displaying a simple text representation of the data.

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Run Statistics")
        Spacer(modifier = Modifier.height(8.dp))

        // Simple visualization for now
        Text(text = "Distance: ${distanceList.joinToString(", ")} meters")
        Text(text = "Time: ${timeList.joinToString(", ")} seconds")
    }
}
