package com.yeminnaing.wakemetransit.domainlayer.repositorie

import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel

interface RouteRepository {

    suspend fun getRoute(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): RouteModel
}
