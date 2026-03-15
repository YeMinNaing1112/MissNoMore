package com.yeminnaing.wakemetransit.domainlayer.usecases.search

import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentPlaceUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    operator fun invoke(): Flow<List<PlaceModel>> {
        return searchRepository.getRecentPlace()
    }

}