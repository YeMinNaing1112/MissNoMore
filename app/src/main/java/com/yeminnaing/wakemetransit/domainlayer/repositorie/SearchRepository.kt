package com.yeminnaing.wakemetransit.domainlayer.repositorie

import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPlaces(
        query: String, countryCode: String? = null,
    ): List<PlaceModel>

    suspend fun saveRecentPlace(place: PlaceModel)

    fun getRecentPlace(): Flow<List<PlaceModel>>

    suspend fun deleteRecentPlace(id: String)

    suspend fun getCountryCode(
        latitude: Double?,
        longitude: Double?,
    ): String?
}