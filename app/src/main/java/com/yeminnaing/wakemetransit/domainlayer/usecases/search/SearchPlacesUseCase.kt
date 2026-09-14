package com.yeminnaing.wakemetransit.domainlayer.usecases.search

import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(
        query: String,
        countryCode: String? = null,
    ): List<PlaceModel> {
        return repository.searchPlaces(
            query = query,
            countryCode = countryCode,
        )
    }
}