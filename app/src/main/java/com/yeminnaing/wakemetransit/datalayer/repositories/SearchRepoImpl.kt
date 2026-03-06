package com.yeminnaing.wakemetransit.datalayer.repositories

import com.yeminnaing.wakemetransit.datalayer.remote.NominatimApi
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import javax.inject.Inject

class SearchRepoImpl @Inject constructor(
    private val api: NominatimApi,
) : SearchRepository {
    override suspend fun searchPlaces(query: String): List<PlaceModel> {
        return  api.searchPlace(query).map {
            PlaceModel(
                id = it.place_id.toString(),
                name = it.display_name,
                lat = it.lat.toDouble() ,
                lon = it.lon.toDouble()

            )
        }
    }

}
