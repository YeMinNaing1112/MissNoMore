package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import android.R.attr.bitmap
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.yeminnaing.wakemetransit.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    MapScreenDesign(modifier)
}


@Composable
fun MapScreenDesign(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION

            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            val mapView = MapView(context)

            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(15.0)
            val startPoint = GeoPoint(16.8409, 96.1735)
            mapView.controller.setCenter(startPoint)
            //Marker

            val locationOverlay = MyLocationNewOverlay(
                GpsMyLocationProvider(context),
                mapView
            )
            val personIcon = ContextCompat.getDrawable(context, R.drawable.currenlocation_blue)

            personIcon?.let {
                val bitmap = it.toBitmap()
                locationOverlay.setDirectionIcon(bitmap)
            }

            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
            locationOverlay.runOnFirstFix {
                val myLocation = locationOverlay.myLocation
                if (myLocation != null) {
                    mapView.post {
                        mapView.controller.setCenter(myLocation)
                    }
                }
            }

            mapView.overlays.add(locationOverlay)

            mapView
        }
    )
}


@Preview
@Composable
private fun MapScreenPreview() {
    MapScreenDesign()
}