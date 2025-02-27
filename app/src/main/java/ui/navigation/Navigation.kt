package com.example.runningtracker.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.runningtracker.ui.screens.HomeScreen
import com.example.runningtracker.ui.screens.RunScreen
import com.example.runningtracker.ui.screens.RunDetailsScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController = navController) }
        composable("run") { RunScreen(navController = navController) }
        composable("run_details/{runId}") { backStackEntry ->
            val runId = backStackEntry.arguments?.getString("runId") ?: ""
            RunDetailsScreen(navController = navController, runId = runId)
        }
    }
}
