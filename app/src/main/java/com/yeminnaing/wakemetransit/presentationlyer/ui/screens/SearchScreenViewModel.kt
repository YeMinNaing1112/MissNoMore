package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.domainlayer.usecases.search.DeleteRecentPlacesUseCase
import com.yeminnaing.wakemetransit.domainlayer.usecases.search.GetRecentPlaceUseCase
import com.yeminnaing.wakemetransit.domainlayer.usecases.search.SaveRecentPlaceUseCase
import com.yeminnaing.wakemetransit.domainlayer.usecases.search.SearchPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    val mSearchPlaceUseCase: SearchPlacesUseCase,
    val mSaveRecentPlacesUseCase: SaveRecentPlaceUseCase,
    val mGetRecentPlaceUseCase: GetRecentPlaceUseCase,
    val mDeleteRecentPlaceUseCase: DeleteRecentPlacesUseCase,
) : ViewModel() {
    private var searchQuery = MutableStateFlow("")
    private var _getPlaceSates = MutableStateFlow<GetPlaceStates>(GetPlaceStates.Empty)
    val getPlaceStates = _getPlaceSates.asStateFlow()

    init {
        searchPlace()
    }

    val recentPlaces = mGetRecentPlaceUseCase.invoke()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRecentPlace(place: PlaceModel) {
        viewModelScope.launch {
            mSaveRecentPlacesUseCase.invoke(place)
        }
    }

    fun deleteRecentPlace(id:String){
        viewModelScope.launch {
            mDeleteRecentPlaceUseCase(id)
        }

    }

    @OptIn(FlowPreview::class)
    private fun searchPlace() {
        viewModelScope.launch {
            searchQuery.debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->

                    if (query.length < 2 || query.isEmpty()) {
                        return@collectLatest
                    }
                    _getPlaceSates.value = GetPlaceStates.Loading

                    try {
                        val result = mSearchPlaceUseCase(query)

                        _getPlaceSates.value = GetPlaceStates.Success(result)
                    } catch (e: Exception) {
                        _getPlaceSates.value = GetPlaceStates.Error(e.message.toString())
                    }
                }

        }
    }

    fun onQueryChange(query: String) {
        searchQuery.value = query
    }

    sealed interface GetPlaceStates {
        data object Empty : GetPlaceStates
        data object Loading : GetPlaceStates
        data class Success(val data: List<PlaceModel>) : GetPlaceStates
        data class Error(val error: String) : GetPlaceStates
    }
}