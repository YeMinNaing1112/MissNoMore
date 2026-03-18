package com.yeminnaing.wakemetransit.datalayer.repositories

import android.content.Context
import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.RouteRepository
import org.osmdroid.util.GeoPoint
class RouteRepoImpl(
    private val context: Context,
) : RouteRepository {
    override suspend fun getRoute(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLong: Double,
    ): RouteModel {
        val roadManager = OSRMRoadmanager(context, "ANDROID")

        val waypoints = arrayListOf(
            GeoPoint(startLat, startLon),
            GeoPoint(endLat, endLong)
        )

        val road = roadManager.getRoad(waypoints)

        return RouteModel(
            points = road.mRouteHigh,
            distance = road.mLength,
            duration = road.mDuration
        )
    }
}