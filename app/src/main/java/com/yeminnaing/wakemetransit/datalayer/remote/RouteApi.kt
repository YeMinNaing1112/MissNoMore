package com.yeminnaing.wakemetransit.datalayer.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApi {
    @GET("route/v1/driving/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates") coordinates: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson"
    ): RouteDto
}