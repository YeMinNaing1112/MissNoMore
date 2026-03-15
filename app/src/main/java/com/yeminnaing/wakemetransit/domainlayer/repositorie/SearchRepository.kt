package com.yeminnaing.wakemetransit.domainlayer.repositorie

import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPlaces(query: String): List<PlaceModel>

    suspend fun saveRecentPlace(place: PlaceModel)

    fun getRecentPlace(): Flow<List<PlaceModel>>

    suspend fun deleteRecentPlace(place: PlaceModel)
}