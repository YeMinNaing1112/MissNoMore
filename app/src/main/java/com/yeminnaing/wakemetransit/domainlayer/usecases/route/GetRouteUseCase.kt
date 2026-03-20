package com.yeminnaing.wakemetransit.domainlayer.usecases.route

import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.RouteRepository
import javax.inject.Inject

class GetRouteUseCase @Inject constructor(
    private val repository: RouteRepository,
) {
    suspend operator fun invoke(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double,
    ): RouteModel {
        return repository.getRoute(startLat, startLon, endLat, endLon)
    }
}