package com.yeminnaing.wakemetransit.presentationlyer.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yeminnaing.wakemetransit.presentationlyer.ui.screens.MapScreen
import com.yeminnaing.wakemetransit.presentationlyer.ui.screens.SearchScreen

@Composable
fun MissNoMoreNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MissNoMoreDestinations.MapScreenDestination
    ) {
        composable<MissNoMoreDestinations.MapScreenDestination> {
            MapScreen()
        }
        composable <MissNoMoreDestinations.SearchScreenDestination>{
            SearchScreen()
        }
    }
}
