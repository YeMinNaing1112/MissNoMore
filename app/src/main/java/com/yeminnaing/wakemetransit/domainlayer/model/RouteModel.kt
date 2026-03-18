package com.yeminnaing.wakemetransit.domainlayer.model

import org.osmdroid.util.GeoPoint

data class RouteModel (
    val points : List<GeoPoint>,
    val distance : Double,
    val duration : Double
)