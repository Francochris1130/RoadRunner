package com.example.runningtracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.runningtracker.ui.screens.HomeScreen
import com.example.runningtracker.ui.screens.RunScreen
import com.example.runningtracker.ui.screens.StatisticsScreen
import com.example.runningtracker.ui.screens.RunViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var runViewModel: RunViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize location client and run view model
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        runViewModel = RunViewModel(application)

        // Request location permissions
        requestLocationPermission()

        setContent {
            MainScreen() // Pass the navController to MainScreen
        }
    }

    // Request permissions for location
    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsGranted ->
            val fineLocationGranted =
                permissionsGranted[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissionsGranted[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                enableLocationUpdates()
            } else {
                // Handle permission denial
            }
        }

        val isFineLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isCoarseLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isFineLocationGranted && !isCoarseLocationGranted) {
            permissionLauncher.launch(permissions)
        } else {
            enableLocationUpdates()
        }
    }

    // Enable location updates
    private fun enableLocationUpdates() {
        // Create a location request with a 1-second interval and high accuracy.
        val locationRequest = LocationRequest.Builder(1000) // 1000 ms interval (1 second)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // High accuracy
            .build()

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Location callback to handle location updates
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // Use locationResult to get the latest location
                    locationResult.locations.firstOrNull()?.let {
                        val lat = it.latitude
                        val lon = it.longitude
                        runViewModel.appendLocation(lat, lon)  // Update the route with the new coordinates
                    }
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null // Using the default Looper (main thread)
            )
        }
    }

}


    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen(navController) }
                composable("run") { RunScreen(navController) }
                composable("statistics") { StatisticsScreen(navController) }
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            BottomNavItem("home", "Home"),
            BottomNavItem("run", "Run"),
            BottomNavItem("statistics", "Stats")
        )

        NavigationBar {
            val currentRoute = navController.currentDestination?.route

            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = { navController.navigate(item.route) },
                    label = { Text(item.label) },
                    icon = { /* Add icons here if needed */ }
                )
            }
        }
    }

    data class BottomNavItem(val route: String, val label: String)
