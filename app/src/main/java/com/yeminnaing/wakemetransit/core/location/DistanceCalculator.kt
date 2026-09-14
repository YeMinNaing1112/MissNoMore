package com.yeminnaing.wakemetransit.core.location

import android.location.Location

fun calculateDistance(
    userLat: Double,
    userLon: Double,
    placeLat: Double,
    placeLon: Double
): Float {

    val result = FloatArray(1)

    Location.distanceBetween(
        userLat,
        userLon,
        placeLat,
        placeLon,
        result
    )

    return result[0]
}