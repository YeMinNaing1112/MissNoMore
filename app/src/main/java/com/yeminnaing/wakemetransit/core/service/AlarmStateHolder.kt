package com.yeminnaing.wakemetransit.core.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmStateHolder @Inject constructor() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    fun trigger() {
        _showDialog.value = true
    }

    fun consumed() {
        _showDialog.value = false
    }

}