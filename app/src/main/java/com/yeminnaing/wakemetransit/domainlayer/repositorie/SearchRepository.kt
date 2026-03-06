package com.yeminnaing.wakemetransit.domainlayer.repositorie

import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel

interface SearchRepository {
    suspend fun searchPlaces(query :String): List<PlaceModel>
}