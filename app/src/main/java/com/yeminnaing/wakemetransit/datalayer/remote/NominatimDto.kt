package com.yeminnaing.wakemetransit.datalayer.remote

import com.google.gson.annotations.SerializedName
import com.yeminnaing.wakemetransit.core.location.calculateDistance

data class NominatimDto(
    val place_id: Long,
    val lat: String,
    val lon: String,
    val display_name: String,
    val name: String?

)

data class ReverseGeocodeDto(
    val address: AddressDto?
)

data class AddressDto(
    @SerializedName("country_code")
    val countryCode: String?
)
