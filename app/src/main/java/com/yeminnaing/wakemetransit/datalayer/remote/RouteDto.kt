package com.yeminnaing.wakemetransit.datalayer.remote

data class RouteDto(
    val code: String,
    val routes: List<Route>,
//    val waypoints: List<Waypoint>
)


data class Route(
    val distance: Double,
    val duration: Double,
    val geometry: Geometry,
//    val legs: List<Leg>,
    val weight: Double,
    val weight_name: String
)


data class Geometry(
    val coordinates: List<List<Double>>,
    val type: String
)



//data class Waypoint(
//    val distance: Double,
//    val hint: String,
//    val location: List<Double>,
//    val name: String
//)
//
//data class Leg(
//    val distance: Double,
//    val duration: Double,
//    val steps: List<Any>,
//    val summary: String,
//    val weight: Double
//)