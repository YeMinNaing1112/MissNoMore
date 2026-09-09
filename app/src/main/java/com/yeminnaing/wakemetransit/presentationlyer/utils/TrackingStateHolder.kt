package com.yeminnaing.wakemetransit.presentationlyer.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingStateHolder @Inject constructor() {
    private val _isTracking= MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    fun setTracking(active: Boolean){
        _isTracking.value= active
    }
}