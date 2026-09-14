package com.yeminnaing.wakemetransit.domainlayer.usecases.search

import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import javax.inject.Inject

class GetCountryCodeUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(
        latitude: Double?,
        longitude: Double?
    ): String? {
        return repository.getCountryCode(
            latitude = latitude,
            longitude = longitude
        )
    }
}