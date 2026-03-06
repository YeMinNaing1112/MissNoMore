package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel


@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val viewModel: SearchScreenViewModel = hiltViewModel()
    val placeStates by viewModel.getPlaceStates.collectAsState()
    SearchScreenDesign(modifier, placeStates, search = {
        viewModel.onQueryChange(it)
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenDesign(
    modifier: Modifier = Modifier,
    placeStates: SearchScreenViewModel.GetPlaceStates,
    search: (query: String) -> Unit,
) {


    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {

        TextField(
            value = query,
            onValueChange = {
                query = it
                search(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search destination...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)

            },
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )

        )

        when (placeStates) {
            is SearchScreenViewModel.GetPlaceStates.Empty -> {}
            is SearchScreenViewModel.GetPlaceStates.Error -> {
                Text("No results found on Map")
            }

            is SearchScreenViewModel.GetPlaceStates.Loading -> {
                Box(
                    modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            }

            is SearchScreenViewModel.GetPlaceStates.Success -> {
                val places = placeStates.data
                LazyColumn(
                    modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    items(places) { place ->
                        Text(place.name)
                    }
                }

            }
        }

    }


}

@Preview
@Composable
private fun SearchScreenDesignPre() {
    SearchScreenDesign(
        placeStates = SearchScreenViewModel.GetPlaceStates.Success(
            data = listOf(
                PlaceModel(
                    id = "1",
                    name = "Siam Square",
                    lat = 1234.4,
                    lon = 454334.5,
                )
            )
        ),
        search = {}
    )
}