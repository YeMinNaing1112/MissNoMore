package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.usecases.search.SearchPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    val mSearchPlaceUseCase: SearchPlacesUseCase,
) : ViewModel() {
    private var _getPlaceSates = MutableStateFlow<GetPlaceStates>(GetPlaceStates.Empty)
    val getPlaceStates = _getPlaceSates.asStateFlow()

    fun searchPlace(query: String) {
        viewModelScope.launch {
            _getPlaceSates.value = GetPlaceStates.Loading

            try {
                val result = mSearchPlaceUseCase(query)

                _getPlaceSates.value = GetPlaceStates.Success(result)
            } catch (e: Exception) {
                _getPlaceSates.value = GetPlaceStates.Error(e.message.toString())
            }
        }
    }

    sealed interface GetPlaceStates {
        data object Empty : GetPlaceStates
        data object Loading : GetPlaceStates
        data class Success(val data: List<PlaceModel>) : GetPlaceStates
        data class Error(val error: String) : GetPlaceStates
    }
}