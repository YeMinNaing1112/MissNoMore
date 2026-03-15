package com.yeminnaing.wakemetransit.domainlayer.usecases.search

import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.yeminnaing.wakemetransit.datalayer.repositories.SearchRepoImpl
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import javax.inject.Inject

class SaveRecentPlaceUseCase @Inject constructor(
 private val  repository: SearchRepository
){
    suspend operator fun invoke(place: PlaceModel){
        repository.saveRecentPlace(place)
    }
}