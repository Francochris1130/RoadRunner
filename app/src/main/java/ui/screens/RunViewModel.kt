package com.example.runningtracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningtracker.data.Run
import com.example.runningtracker.data.RunDatabase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RunViewModel(application: Application) : AndroidViewModel(application) {
    private val runDao = RunDatabase.getDatabase(application).runDao()

    private val _allRuns = runDao.getAllRuns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRuns: StateFlow<List<Run>> = _allRuns

    private var startTime: Long = 0L

    var currentRun = Run(
        id = 0,
        distanceInMeters = 0,
        timeInMillis = 0L,
        avgSpeed = 0f,
        routeCoordinates = mutableListOf(),
        date = System.currentTimeMillis()  // Just in case, but we'll overwrite in stopRun
    )

    fun startRun() {
        startTime = System.currentTimeMillis()
        currentRun = Run(
            id = 0,
            distanceInMeters = 0,
            timeInMillis = 0L,
            avgSpeed = 0f,
            routeCoordinates = mutableListOf(),
            date = startTime  // Set initial date
        )
    }

    fun updateRun(distance: Int, timeInSeconds: Long, speed: Float) {
        currentRun = currentRun.copy(
            distanceInMeters = distance,
            timeInMillis = timeInSeconds * 1000, // Convert to milliseconds
            avgSpeed = speed
        )
    }

    fun appendLocation(latitude: Double, longitude: Double) {
        currentRun.routeCoordinates.add(LatLng(latitude, longitude))
    }

    fun stopRun() {
        val endTime = System.currentTimeMillis()
        val totalDuration = endTime - startTime

        currentRun = currentRun.copy(
            timeInMillis = totalDuration,
            date = endTime // Capture actual date at the moment of saving
        )

        viewModelScope.launch {
            runDao.insertRun(currentRun)
        }
    }
}
