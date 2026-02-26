package com.yeminnaing.wakemetransit.presentationlyer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            // 1. State to track if permission is granted
            var hasLocationPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            // 2. Permission Launcher
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                hasLocationPermission = isGranted
            }

            // 3. Request permission if not granted
            LaunchedEffect(Unit) {
                if (!hasLocationPermission) {
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            // 4. Map UI
            val singapore = LatLng(1.35, 103.87)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(singapore, 11f)
            }

            // CRITICAL: isMyLocationEnabled must ONLY be true if permission is granted
            val mapProperties by remember(hasLocationPermission) {
                mutableStateOf(
                    MapProperties(isMyLocationEnabled = hasLocationPermission)
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties
            )
        }
    }
}
