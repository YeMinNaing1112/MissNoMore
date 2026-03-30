package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeminnaing.wakemetransit.domainlayer.model.RouteModel
import com.yeminnaing.wakemetransit.domainlayer.usecases.route.GetRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val routeUserCase: GetRouteUseCase,
) : ViewModel() {
    private val _route = MutableStateFlow<RouteModel?>(null)

    val route = _route.asStateFlow()

    fun getRoute(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double,
    ) {
        viewModelScope.launch {
            try {
                val result = routeUserCase.invoke(startLat, startLon, endLat, endLon)

                _route.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}