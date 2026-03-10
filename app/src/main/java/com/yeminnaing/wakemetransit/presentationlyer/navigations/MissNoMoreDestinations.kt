package com.yeminnaing.wakemetransit.presentationlyer.navigations

import kotlinx.serialization.Serializable

@Serializable
sealed class MissNoMoreDestinations {
    @Serializable
    data class MapScreenDestination(
        val id: String? = null,
        val name: String? = null,
        val lat: Double? = null,
        val lon: Double? = null,

        ) : MissNoMoreDestinations()

    @Serializable
    data object SearchScreenDestination : MissNoMoreDestinations()
}