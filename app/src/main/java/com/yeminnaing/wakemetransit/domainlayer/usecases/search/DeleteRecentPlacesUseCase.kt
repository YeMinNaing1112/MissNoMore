package com.yeminnaing.wakemetransit.domainlayer.usecases.search

import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import javax.inject.Inject

class DeleteRecentPlacesUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke (id:String){
        searchRepository.deleteRecentPlace(id)
    }
}