package com.example.runningtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

@Database(entities = [Run::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao

    companion object {
        @Volatile
        private var INSTANCE: RunDatabase? = null

        fun getDatabase(context: Context): RunDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunDatabase::class.java,
                    "run_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    class Converters {
        // Convert List<LatLng> to String
        @TypeConverter
        fun fromLatLngList(latLngList: List<LatLng>?): String? {
            return latLngList?.let {
                Gson().toJson(it)  // Use Gson to convert List<LatLng> to JSON
            }
        }

        // Convert String back to List<LatLng>
        @TypeConverter
        fun toLatLngList(latLngString: String?): List<LatLng>? {
            val listType = object : TypeToken<List<LatLng>>() {}.type
            return latLngString?.let {
                Gson().fromJson(it, listType)
            }
        }
    }

}
