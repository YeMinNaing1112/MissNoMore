package com.yeminnaing.wakemetransit.presentationlyer.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.yeminnaing.wakemetransit.presentationlyer.ui.screens.MapScreen
import com.yeminnaing.wakemetransit.presentationlyer.ui.screens.SearchScreen

@Composable
fun MissNoMoreNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MissNoMoreDestinations.MapScreenDestination()
    ) {
        composable<MissNoMoreDestinations.MapScreenDestination> { backStack ->
            val mapScreen: MissNoMoreDestinations.MapScreenDestination = backStack.toRoute()
            MapScreen(
                lat = mapScreen.lat,
                lon = mapScreen.lon,
                navHostController = navController
            )
        }
        composable<MissNoMoreDestinations.SearchScreenDestination> {
            SearchScreen(navHost = navController)
        }
    }
}
