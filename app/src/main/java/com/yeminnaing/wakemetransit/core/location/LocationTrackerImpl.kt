package com.yeminnaing.wakemetransit.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


//class LocationTrackerImpl @Inject  constructor(
//    @ApplicationContext private val context: Context,
//) : LocationTracker {
//    override fun getLocationUpdate(): Flow<GeoPoint> = callbackFlow {
//        val mapView = MapView(context)
//
//        val overlay = MyLocationNewOverlay(
//            GpsMyLocationProvider(context),
//            mapView
//        )
//        overlay.enableMyLocation()
//
//        val handler = Handler(Looper.getMainLooper())
//        val runnable = object : Runnable{
//            override fun run() {
//                overlay.myLocation?.let {
//                    trySend(it)
//                }
//                handler.postDelayed(this,3000)
//
//            }
//
//        }
//        handler.post(runnable)
//
//        awaitClose {
//            handler.removeCallbacks(runnable)
//        }
//
//    }
//
//}

class LocationTrackerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationTracker {

    override fun getLocationUpdate(): Flow<Location> = callbackFlow {

        val client = LocationServices.getFusedLocationProviderClient(context)
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000 // 3 seconds
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(it)
                }
            }
        }

        client.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}
