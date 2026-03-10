package com.yeminnaing.wakemetransit.domainlayer.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceModel(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
)