package com.yeminnaing.wakemetransit.datalayer.remote

data class NominatimDto(
    val place_id: Long,
    val lat: String,
    val lon: String,
    val display_name: String,
    val name: String?

)