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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.yeminnaing.wakemetransit.R
import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel
import com.yeminnaing.wakemetransit.presentationlyer.navigations.MissNoMoreDestinations
import com.yeminnaing.wakemetransit.presentationlyer.utils.startService
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    lat: Double?,
    lon: Double?,
    navHostController: NavHostController,
) {
    val viewModel: MapScreenViewModel = hiltViewModel()
    val route by viewModel.route.collectAsState()
    MapScreenDesign(
        modifier = modifier, lat,
        lon,
        navigateToSearchScreen = {
            navHostController.navigate(MissNoMoreDestinations.SearchScreenDestination)
        },
        route,
        getRoute = { startLat, startLon, endLat, endLon ->
            viewModel.getRoute(
                startLat, startLon, endLat, endLon
            )
        }
    )
}


@Composable
fun MapScreenDesign(
    modifier: Modifier = Modifier, lat: Double?,
    lon: Double?,
    navigateToSearchScreen: () -> Unit,
    route: RouteModel?,
    getRoute: (startLat: Double, startLon: Double, endLat: Double, endLon: Double) -> Unit,
) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION

            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var serviceStarted by remember {
        mutableStateOf(false)
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
        val handler = remember {
            android.os.Handler(
                android.os.Looper.getMainLooper()
            )
        }


        DisposableEffect(Unit) {
            onDispose {
                handler.removeCallbacksAndMessages(null)
            }
        }

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = {
                val mapView = MapView(context)

                mapView.setMultiTouchControls(true)
                mapView.controller.setZoom(15.0)

                val locationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(context), mapView
                )
                val personIcon = ContextCompat.getDrawable(context, R.drawable.currenlocation_blue)
                val distinationIcon =
                    ContextCompat.getDrawable(context, R.drawable.destination_blue)

                personIcon?.let {
                    val bitmap = it.toBitmap()
                    locationOverlay.setDirectionIcon(bitmap)
                }
                val marker = Marker(mapView)
                locationOverlay.enableMyLocation()
                locationOverlay.runOnFirstFix {
                    val myLocation = locationOverlay.myLocation

                    if (lat != null && lon != null && myLocation != null) {
                        val boundingBox = BoundingBox.fromGeoPoints(
                            listOf(myLocation, GeoPoint(lat, lon))
                        )
                        mapView.post {
                            mapView.zoomToBoundingBox(boundingBox, true, 150)
                        }

                        getRoute(
                            myLocation.latitude,
                            myLocation.longitude,
                            lat,
                            lon,
                        )
                        if (!serviceStarted) {

                            startService(
                                context,
                                lat,
                                lon
                            )

                            serviceStarted = true
                        }
                    } else if (myLocation != null) {
                        mapView.post {
                            mapView.controller.setCenter(myLocation)
                        }
                    }
                }
                //DistinationMarker

                if (lat != null && lon != null) {
                    marker.position = GeoPoint(lat, lon)
                    marker.title = "Distination"
                    marker.icon = distinationIcon
                    mapView.overlays.removeAll { it is Marker }
                    mapView.overlays.add(marker)
                }

                mapView.overlays.add(locationOverlay)
                //Live Update & refresh route

                val runnable = object : Runnable {
                    override fun run() {
                        val mylocation = locationOverlay.myLocation
                        if (
                            mylocation != null &&
                            lat != null &&
                            lon != null
                        ) {

                            getRoute(
                                mylocation.latitude,
                                mylocation.longitude,
                                lat, lon
                            )
                        }
                        handler.postDelayed(this, 2000)
                    }

                }
                handler.post(runnable)


                mapView
            }, update = { mapView ->
                //draw PolyLine
                route?.let {
                    drawRoute(mapView, it)
                }
            }
        )
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


fun drawRoute(
    mapView: MapView,
    route: RouteModel,
) {
    val getPoint = route.points.map {
        GeoPoint(it.first, it.second)
    }
    mapView.overlays.removeAll { it is Polyline }

    val polyLine = Polyline().apply {
        setPoints(getPoint)
        outlinePaint.color = android.graphics.Color.BLUE
        outlinePaint.strokeWidth = 8f
    }
    mapView.overlays.add(polyLine)
    mapView.invalidate()
}

@Preview
@Composable
private fun MapScreenPreview() {
    MapScreenDesign(
        lat = null, lon = null,
        navigateToSearchScreen = {},
        route = null,
        getRoute = { _, _, _, _ -> }
    )
}
