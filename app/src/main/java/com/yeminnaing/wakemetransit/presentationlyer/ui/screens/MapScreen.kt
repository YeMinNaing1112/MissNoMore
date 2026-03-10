package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.yeminnaing.wakemetransit.R
import com.yeminnaing.wakemetransit.presentationlyer.navigations.MissNoMoreDestinations
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    lat: Double?,
    lon: Double?,
    navHostController: NavHostController,
) {
    MapScreenDesign(
        modifier = modifier, lat,
        lon,
        navigateToSearchScreen = { navHostController.navigate(MissNoMoreDestinations.SearchScreenDestination) }
    )
}


@Composable
fun MapScreenDesign(
    modifier: Modifier = Modifier, lat: Double?,
    lon: Double?,
    navigateToSearchScreen: () -> Unit,
) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION

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
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        AndroidView(
            modifier = modifier.fillMaxSize(), factory = {
                val mapView = MapView(context)

                mapView.setMultiTouchControls(true)
                mapView.controller.setZoom(15.0)

                val locationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(context), mapView
                )
                val personIcon = ContextCompat.getDrawable(context, R.drawable.currenlocation_blue)
                val distinationIcon = ContextCompat.getDrawable(context,R.drawable.destination_blue)

                personIcon?.let {
                    val bitmap = it.toBitmap()
                    locationOverlay.setDirectionIcon(bitmap)
                }

                //DistinationMarker
                val marker = Marker(mapView)
                if (lat != null && lon != null) {

                    marker.position = GeoPoint(lat, lon)
                    marker.title = "Distination"
                    marker.icon = distinationIcon
                    mapView.overlays.add(marker)
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
            })
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .clickable { navigateToSearchScreen() },
            shape = RoundedCornerShape(30.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Search destination")
            }
        }
    }
}


@Preview
@Composable
private fun MapScreenPreview() {
    MapScreenDesign(
        modifier = Modifier, lat = null,
        lon = null,
        navigateToSearchScreen = {}
    )
}