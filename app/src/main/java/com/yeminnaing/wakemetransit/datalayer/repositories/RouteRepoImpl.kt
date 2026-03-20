package com.yeminnaing.wakemetransit.datalayer.repositories

import android.content.Context
import com.yeminnaing.wakemetransit.datalayer.remote.RouteApi
import com.yeminnaing.wakemetransit.datalayer.toRouteModel
import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.RouteRepository
import javax.inject.Inject
class RouteRepoImpl @Inject constructor(
    private val api : RouteApi
) : RouteRepository {
    override suspend fun getRoute(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double,
    ): RouteModel {
       val coordinates = "$startLon,$startLat;$endLon,$endLat"

        val result = api.getRoute(coordinates)

        return result.toRouteModel()
    }


}