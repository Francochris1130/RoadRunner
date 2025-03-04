package com.example.runningtracker.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromLatLngList(value: List<LatLng>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLatLngList(value: String): List<LatLng> {
        val listType = object : TypeToken<List<LatLng>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
