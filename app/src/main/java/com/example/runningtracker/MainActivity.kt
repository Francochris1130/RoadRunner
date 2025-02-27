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
import com.google.android.gms.maps.GoogleMap

class MainActivity : ComponentActivity() {
    private lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MainScreen(navController)
        }
    }

    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsGranted ->
            val fineLocationGranted = permissionsGranted[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissionsGranted[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                enableMyLocation()
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
            enableMyLocation()
        }
    }

    private fun enableMyLocation() {
        if (::googleMap.isInitialized) {
            try {
                googleMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
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
