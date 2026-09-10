package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.yeminnaing.wakemetransit.presentationlyer.utils.stopService
import kotlinx.coroutines.delay
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
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
    val tracking by viewModel.isTracking.collectAsState()
    val alarmState by viewModel.alarmState.collectAsState()

    MapScreenDesign(modifier = modifier, lat, lon, navigateToSearchScreen = {
        navHostController.navigate(MissNoMoreDestinations.SearchScreenDestination)
    }, route, getRoute = { startLat, startLon, endLat, endLon ->
        viewModel.getRoute(
            startLat, startLon, endLat, endLon
        )
    }, isTracking = tracking, clearRoute = {
        viewModel.clearRoute()
    }, alarmState, stopAlarm = { viewModel.stopAlarm() })
}


@Composable
fun MapScreenDesign(
    modifier: Modifier = Modifier, lat: Double?,
    lon: Double?,
    navigateToSearchScreen: () -> Unit,
    route: RouteModel?,
    getRoute: (startLat: Double, startLon: Double, endLat: Double, endLon: Double) -> Unit,
    isTracking: Boolean,
    clearRoute: () -> Unit,
    alarmState: Boolean,
    stopAlarm: () -> Unit,
) {
    val context = LocalContext.current

    var mapView by remember { mutableStateOf<MapView?>(null) }

    var isFollowing by remember { mutableStateOf(true) }

    var showCancelTrackingSheet by remember {
        mutableStateOf(false)
    }

    var trackedDestinations by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION

            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var locationOverLay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    val distinationIcon = remember {
        ContextCompat.getDrawable(context, R.drawable.destination_blue)
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
    //Draw Route
    LaunchedEffect(trackedDestinations) {
        val dest = trackedDestinations ?: return@LaunchedEffect
        while (true) {
            val myLocation = locationOverLay?.myLocation
            if (myLocation != null) {
                getRoute(myLocation.latitude, myLocation.longitude, dest.first, dest.second)
            }
            delay(2000)
        }
    }

    //Cancel Route and Destiantion by Notification
    LaunchedEffect(isTracking) {
        if (!isTracking) {
            trackedDestinations = null
            clearRoute()
            showCancelTrackingSheet = false
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

        AndroidView(modifier = modifier.fillMaxSize(), factory = {
            val map = MapView(context)
            mapView = map
            map.setMultiTouchControls(true)
            map.controller.setZoom(15.0)

            val overlay = MyLocationNewOverlay(
                GpsMyLocationProvider(context), map
            )
            locationOverLay = overlay

            val personIcon = ContextCompat.getDrawable(context, R.drawable.currenlocation_blue)
            val distinationIcon = distinationIcon

            personIcon?.let {
                val bitmap = it.toBitmap()
                overlay.setDirectionIcon(bitmap)
            }
            val marker = Marker(map)
            overlay.enableMyLocation()


            map.addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    if (!overlay.isFollowLocationEnabled) {
                        isFollowing = false
                    }
                    return false
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    return false
                }

            })
            overlay.runOnFirstFix {
                val myLocation = overlay.myLocation

                if (lat != null && lon != null && myLocation != null) {
                    val boundingBox = BoundingBox.fromGeoPoints(
                        listOf(myLocation, GeoPoint(lat, lon))
                    )
                    map.post {
                        map.zoomToBoundingBox(boundingBox, true, 150)
                    }

                    getRoute(
                        myLocation.latitude,
                        myLocation.longitude,
                        lat,
                        lon,
                    )
                    if (trackedDestinations != lat to lon) {
                        startService(context, lat, lon)
                        trackedDestinations = lat to lon
                    }
                } else if (myLocation != null) {
                    map.post {
                        map.controller.setCenter(myLocation)
                    }
                }
            }
            //DistinationMarker
            if (lat != null && lon != null) {
                marker.position = GeoPoint(lat, lon)
                marker.title = "Distination"
                marker.icon = distinationIcon
                map.overlays.removeAll { it is Marker }
                map.overlays.add(marker)
            }
            map.overlays.add(overlay)

            //Live Update & refresh route`
            val runnable = object : Runnable {
                override fun run() {
                    val myLocation = overlay.myLocation
                    if (myLocation != null && lat != null && lon != null) {

                        getRoute(
                            myLocation.latitude, myLocation.longitude, lat, lon
                        )
                    }
                    handler.postDelayed(this, 2000)
                }

            }
            handler.post(runnable)


            map
        }, update = { mapView ->
            // Draw Polyline and Add Destination Marker
            mapView.overlays.removeAll { it is Marker }
            trackedDestinations?.let { (lat, lon) ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(lat, lon)
                    title = "Destination"
                    icon = distinationIcon
                }
                mapView.overlays.add(marker)
            }
            //Remove Polyline if there is no Destination
            mapView.overlays.removeAll { it is Polyline }
            if (trackedDestinations != null) {
                route?.let { drawRoute(mapView, it) }
            }

            mapView.invalidate()
        })
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { navigateToSearchScreen() }, shape = RoundedCornerShape(30.dp)
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
            //FLowing the User Position
            if (!isFollowing) {
                FloatingActionButton(
                    onClick = {
                        val myLocation = locationOverLay?.myLocation
                        if (myLocation != null) {
                            mapView?.controller?.animateTo(
                                myLocation,
                                18.0,
                                800L
                            )
                        }
                        locationOverLay?.enableFollowLocation()
                        isFollowing = true
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp, end = 16.dp)

                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter")
                }
            }
        }

        // Cancel Tracking Dialog Box to Cancel the Destination , Service and Route
        LaunchedEffect(trackedDestinations) {
            if (trackedDestinations != null) {
                showCancelTrackingSheet = true
            }
        }
        if (showCancelTrackingSheet) {

            CancelTrackingSheet(
                modifier = Modifier.align(Alignment.BottomCenter),
                onCancelTracking = {
                    stopService(context)
                    trackedDestinations = null
                    clearRoute()
                    showCancelTrackingSheet = false
                }
            )
        }

        //Cancel Alarm Arrival
        if (alarmState) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Wake up!") },
                text = { Text("This is your stop") },
                confirmButton = {
                    Button(onClick = { stopAlarm() }) {
                        Text("Stop")
                    }
                }
            )
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelTrackingSheet(
    modifier: Modifier = Modifier,
    onCancelTracking: () -> Unit,
) {
    Log.d("TrackingDebug", "CancelTrackingSheet composing")
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text("Cancel tracking?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Are you sure you want to stop tracking this destination?",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onCancelTracking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancel tracking")
            }
        }
    }
}

@Preview
@Composable
private fun CancelTrackingSheetPrev() {
    CancelTrackingSheet(onCancelTracking = {})
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
        lat = null,
        lon = null,
        navigateToSearchScreen = {},
        route = null,
        getRoute = { _, _, _, _ -> },
        isTracking = true,
        clearRoute = {},
        alarmState = true,
        stopAlarm = {}
    )
}
