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
        putExtra("lat", lat)
        putExtra("lon", lon)
    }

    ContextCompat.startForegroundService(context, intent)
}