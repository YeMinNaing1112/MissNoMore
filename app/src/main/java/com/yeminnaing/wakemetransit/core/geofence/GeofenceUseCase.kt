package com.yeminnaing.wakemetransit.core.geofence

import android.R.attr.radius
import android.location.Location
import androidx.compose.ui.geometry.CornerRadius
import com.yeminnaing.wakemetransit.core.location.LocationTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class GeofenceUseCase @Inject constructor(
     private val tracker : LocationTracker
) {
    fun monitor (
        destination : Location,
    ): Flow<Float>{
        return tracker.getLocationUpdate()
            .map { current->current.distanceTo(destination) }
            .distinctUntilChanged()
    }
}