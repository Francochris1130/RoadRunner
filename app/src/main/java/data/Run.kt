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
    val id: Long = 0,
    var distanceInMeters: Int,
    var timeInMillis: Long,
    var avgSpeed: Float,
    val routeCoordinates: List<LatLng>, // List of LatLng points for the route
    val date: Long = System.currentTimeMillis() // Store the date the run was created
)
fun formatDateInEST(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val estTimeZone = TimeZone.getTimeZone("America/New_York") // EST timezone
    dateFormat.timeZone = estTimeZone
    return dateFormat.format(Date(timestamp)) // Format the UTC timestamp in EST
}