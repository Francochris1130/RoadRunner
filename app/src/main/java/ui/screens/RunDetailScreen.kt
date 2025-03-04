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
    val allRuns by runViewModel.allRuns.collectAsState(initial = emptyList())
    val run = allRuns.find { it.id.toString() == runId }

    if (run == null) {
        Text("Run not found!")
        return
    }

    val context = LocalContext.current
    val runLocations = run.routeCoordinates.map { LatLng(it.latitude, it.longitude) }

    val cameraPositionState = rememberCameraPositionState {
        position = if (runLocations.isNotEmpty()) {
            CameraPosition.fromLatLngZoom(runLocations.first(), 15f)
        } else {
            CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Run Details", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text("Run Date: ${run.timeInMillis}")
        Text("Distance: %.2f miles".format(run.distanceInMeters / 1609.34f))
        Text("Avg Speed: %.2f mph".format(run.avgSpeed * 0.621371))

        Spacer(Modifier.height(16.dp))

        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            runLocations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Run Point"
                )
            }

            Polyline(
                points = runLocations,
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 5f
            )
        }
    }
}
