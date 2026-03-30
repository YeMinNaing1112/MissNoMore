package com.yeminnaing.wakemetransit.core.location

import android.content.Context
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import javax.inject.Inject

class LocationTrackerImpl @Inject  constructor(
    @ApplicationContext private val context: Context,
) : LocationTracker {
    override fun getLocationUpdate(): Flow<GeoPoint> = callbackFlow {
        val mapView = MapView(context)

        val overlay = MyLocationNewOverlay(
            GpsMyLocationProvider(context),
            mapView
        )
        overlay.enableMyLocation()

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable{
            override fun run() {
                overlay.myLocation?.let {
                    trySend(it)
                }
                handler.postDelayed(this,3000)

            }

        }
        handler.post(runnable)

        awaitClose {
            handler.removeCallbacks(runnable)
        }

    }

}