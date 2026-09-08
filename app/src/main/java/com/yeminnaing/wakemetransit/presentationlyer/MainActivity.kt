package com.yeminnaing.wakemetransit.presentationlyer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.yeminnaing.wakemetransit.presentationlyer.navigations.MissNoMoreNavGraph
import com.yeminnaing.wakemetransit.presentationlyer.ui.screens.SearchScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        org.osmdroid.config.Configuration.getInstance()
            .load(applicationContext, getSharedPreferences("osm_pref", MODE_PRIVATE))

        val (initialLat, initialLon) = extractDestination(intent)

        setContent {
            MissNoMoreNavGraph(startLat = initialLat, startLon = initialLon)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun extractDestination(intent: Intent): Pair<Double?, Double?> {
        val lat = intent.getDoubleExtra("lat", Double.NaN).takeIf { !it.isNaN() }
        val lon = intent.getDoubleExtra("lon", Double.NaN).takeIf { !it.isNaN() }
        return lat to lon
    }
}