package com.yeminnaing.wakemetransit.presentationlyer.navigations

import kotlinx.serialization.Serializable

@Serializable
 sealed class MissNoMoreDestinations {
     @Serializable
     data object MapScreenDestination: MissNoMoreDestinations()

    @Serializable
    data object SearchScreenDestination: MissNoMoreDestinations()
}