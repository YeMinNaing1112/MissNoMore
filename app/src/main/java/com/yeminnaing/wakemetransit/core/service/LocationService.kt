package com.yeminnaing.wakemetransit.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat
import com.yeminnaing.wakemetransit.R
import com.yeminnaing.wakemetransit.core.NotificationHelper
import com.yeminnaing.wakemetransit.core.geofence.GeofenceUseCase
import com.yeminnaing.wakemetransit.presentationlyer.MainActivity
import com.yeminnaing.wakemetransit.presentationlyer.utils.TrackingStateHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    @Inject
    lateinit var geofenceUseCase: GeofenceUseCase

    @Inject
    lateinit var notificationHelper: NotificationHelper
    private val scope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.IO
    )

    @Inject
    lateinit var trackingStateHolder: TrackingStateHolder

    private var monitoringJob: Job? = null

    companion object {
        const val ACTION_START = "com.yeminnaing.wakemetransit.ACTION_START"
        const val ACTION_STOP = "com.yeminnaing.wakemetransit.ACTION_STOP"
        const val EXTRA_LAT = "lat"
        const val EXTRA_LON = "lon"
        private const val CHANNEL_ID = "LocationService"
        private const val NOTIFICATION_ID = 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTracking()
            return START_NOT_STICKY
        }

        val lat = intent?.getDoubleExtra("lat", Double.NaN)
        val lon = intent?.getDoubleExtra("lon", Double.NaN)

        if (lat == null || lon == null ||
            lat.isNaN() || lon.isNaN()
        ) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        startForeground()
        trackingStateHolder.setTracking(true)
        val destination = Location("").apply {
            latitude = lat
            longitude = lon
        }

        monitoringJob?.cancel() //cancel old monitoring

        monitoringJob = scope.launch {
            geofenceUseCase.monitor(destination)
                .collect { isInside ->
                    if (isInside) {
                        notificationHelper.showAlarm()
                        stopSelf()
                    }

                }
        }
        return START_STICKY
    }

    private fun stopTracking() {
        monitoringJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        trackingStateHolder.setTracking(false)
        stopSelf()
    }

    override fun onDestroy() {
        monitoringJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }


    private fun startForeground() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val stopIntent = Intent(this, LocationService::class.java).apply { action = ACTION_STOP }

        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking location")
            .setContentText("Monitoring your stop...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(contentPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "cancel", stopPendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }


    override fun onBind(p0: Intent?) = null
}
