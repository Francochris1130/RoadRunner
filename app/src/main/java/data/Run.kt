package com.example.runningtracker.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Entity(tableName = "run_table")
data class Run(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var distanceInMeters: Int,
    var timeInMillis: Long,
    var avgSpeed: Float,
    val routeCoordinates: MutableList<LatLng> = mutableListOf(), // List of LatLng points for the route
    val date: Long = System.currentTimeMillis() // Store the date the run was created
)
fun formatDateInEST(timeInMillis: Long): String {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("America/New_York")
    return dateFormat.format(Date(timeInMillis))
}