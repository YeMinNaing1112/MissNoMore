package com.yeminnaing.wakemetransit.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat
import com.yeminnaing.wakemetransit.R
import com.yeminnaing.wakemetransit.core.NotificationHelper
import com.yeminnaing.wakemetransit.core.geofence.GeofenceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    @Inject
    lateinit var geofenceUseCase: GeofenceUseCase

    @Inject
    lateinit var notificationHelper: NotificationHelper
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        val lat = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
        val lon = intent?.getDoubleExtra("lon", 0.0) ?: 0.0
        val destination = GeoPoint(lat, lon)

        scope.launch {
            geofenceUseCase.monitor(destination.toLocation())
                .collect { isInside ->
                    if (isInside) {
                        notificationHelper.showAlarm()
                        stopSelf()
                    }

                }
        }
        return START_STICKY
    }

    private fun startForeground() {
        val channelId = "LocationService"
        val channel = NotificationChannel(
            channelId,
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking location")
            .setContentText("Monitoring your stop...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(2, notification)
    }


    override fun onBind(p0: Intent?) = null
}

fun GeoPoint.toLocation(): Location {
    return Location("").apply {
        latitude = this@toLocation.latitude
        longitude = this@toLocation.longitude
    }
}