package com.yeminnaing.wakemetransit.core.service

import android.app.Service
import android.content.Intent
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
        val lat = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
        val lon = intent?.getDoubleExtra("lon", 0.0) ?: 0.0
        val destination = GeoPoint(lat, lon)

        scope.launch {
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

    override fun onBind(p0: Intent?) = null
}