package com.yeminnaing.wakemetransit.datalayer.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {

    @GET("search")
    suspend fun searchPlace(
        @Query("q") query: String,
        @Query("format") format: String = "jsonv2",
        @Query( "accept-language") language: String = "en",
    ): List<NominatimDto>
}
