package com.yeminnaing.wakemetransit.core.location

import kotlinx.coroutines.flow.Flow
import org.osmdroid.util.GeoPoint

interface LocationTracker {
    fun getLocationUpdate(): Flow<GeoPoint>
}