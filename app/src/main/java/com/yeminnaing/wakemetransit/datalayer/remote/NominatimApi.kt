package com.yeminnaing.wakemetransit.datalayer.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {

    @GET("search")
    suspend fun searchPlace(
        @Query("q") query: String,
        @Query("format") format: String = "jsonv2",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("countrycodes") countryCodes: String? = null,
        @Query("accept-language") acceptLanguage: String = "en"
    ): List<NominatimDto>

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("format") format: String = "jsonv2",
        @Query("addressdetails") addressDetails: Int = 1
    ): ReverseGeocodeDto

}
