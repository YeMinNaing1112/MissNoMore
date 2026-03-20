package com.yeminnaing.wakemetransit.datalayer

import com.yeminnaing.wakemetransit.datalayer.remote.RouteDto
import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel

fun RouteDto.toRouteModel(): RouteModel {
    val coordinates = routes.firstOrNull()?.geometry?.coordinates ?: emptyList()

    val points = coordinates.map {
        Pair(it[1], it[0])
    }

    return RouteModel(points)
}