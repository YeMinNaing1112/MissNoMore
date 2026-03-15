package com.yeminnaing.wakemetransit.datalayer.repositories

import com.yeminnaing.wakemetransit.datalayer.local.RecentPlaceDao
import com.yeminnaing.wakemetransit.datalayer.local.RecentPlaceEntity
import com.yeminnaing.wakemetransit.datalayer.local.mapper.toPlaceModel
import com.yeminnaing.wakemetransit.datalayer.remote.NominatimApi
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepoImpl @Inject constructor(
    private val api: NominatimApi,
    private val dao: RecentPlaceDao,
) : SearchRepository {
    override suspend fun searchPlaces(query: String): List<PlaceModel> {
        return api.searchPlace(query).map {
            PlaceModel(
                id = it.place_id.toString(),
                name = it.display_name,
                lat = it.lat.toDouble(),
                lon = it.lon.toDouble()

            )
        }
    }

    override suspend fun saveRecentPlace(place: PlaceModel) {
        dao.insert(
            RecentPlaceEntity(
                id = place.id,
                lat = place.lat,
                lon = place.lon,
                name = place.name
            )
        )
    }

    override fun getRecentPlace(): Flow<List<PlaceModel>> {
        return dao.getRecentPlace().map { placeList ->
            placeList.map {
                it.toPlaceModel()
            }

        }
    }

    override suspend fun deleteRecentPlace(place: PlaceModel) {
        //
    }

}

