package com.yeminnaing.wakemetransit.presentationlyer.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yeminnaing.wakemetransit.core.service.LocationService

fun startService(
    context: Context,
    lat: Double,
    lon: Double
) {
    val intent = Intent(context, LocationService::class.java).apply {
        action= LocationService.ACTION_START
        putExtra(LocationService.EXTRA_LAT, lat)
        putExtra(LocationService.EXTRA_LON, lon)
    }

    ContextCompat.startForegroundService(context, intent)
}
fun stopService(context: Context) {
    val intent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
    }
    context.startService(intent)
}