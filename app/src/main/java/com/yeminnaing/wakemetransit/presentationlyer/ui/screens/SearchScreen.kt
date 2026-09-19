package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.yeminnaing.wakemetransit.R
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel
import com.yeminnaing.wakemetransit.presentationlyer.navigations.MissNoMoreDestinations


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    lat: Double?,
    lon: Double?,
    navHost: NavHostController,
) {
    val viewModel: SearchScreenViewModel = hiltViewModel()
    val placeStates by viewModel.getPlaceStates.collectAsState()
    val recentPlace by viewModel.recentPlaces.collectAsStateWithLifecycle()

    LaunchedEffect(lat, lon) {
        viewModel.getCountryCode(
            latitude = lat,
            longitude = lon
        )
    }

    SearchScreenDesign(
        modifier,
        placeStates,
        search = {
            viewModel.onQueryChange(it)
        },
        navigateToMapScreen = {
            navHost.navigate(
                MissNoMoreDestinations.MapScreenDestination(
                    lat = it.lat,
                    lon = it.lon,
                    id = it.id,
                    name = it.name
                )
            ) {
                popUpTo(navHost.graph.findStartDestination().id) {
                    inclusive = true
                    saveState = false
                }
                launchSingleTop = true
            }
        },
        recentPlace,
        addToRecent = {
            viewModel.addRecentPlace(it)
        },
        deleteRecent = {
            viewModel.deleteRecentPlace(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenDesign(
    modifier: Modifier = Modifier,
    placeStates: SearchScreenViewModel.GetPlaceStates,
    search: (query: String) -> Unit,
    navigateToMapScreen: (place: PlaceModel) -> Unit,
    recentPlace: List<PlaceModel>,
    addToRecent: (place: PlaceModel) -> Unit,
    deleteRecent: (id: String) -> Unit,
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
                .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            placeholder = { Text("Search destination...", color = colorResource(R.color.Blue)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = colorResource(R.color.Blue)
                )

            },
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )

        )
        var selectPlace by remember {
            mutableStateOf<PlaceModel?>(null)
        }
        if (query.isEmpty()) {
            if (recentPlace.isEmpty()) {
                NoRecentSearchCard()
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    items(
                        items = recentPlace,
                        key = { place -> place.id }
                    ) { place ->
                        RecentPlaceItem(
                            place = place,
                            onClick = {
                                selectPlace = place
                            },
                            onDelete = {
                               deleteRecent(place.id)
                            }
                        )
                    }
                }
                selectPlace?.let { it ->
                    CancelTrackingSheet(
                        modifier = modifier,
                        onDismiss = {
                            selectPlace = null
                        },
                        name = it.name,
                    ) { navigateToMapScreen(it) }
                }
            }


        }

        when (placeStates) {
            is SearchScreenViewModel.GetPlaceStates.Empty -> {
                if (query.isNotEmpty()) {
                    NoSearchResultsCard()
                }
            }

            is SearchScreenViewModel.GetPlaceStates.Error -> {
                NoSearchResultsCard()
            }

            is SearchScreenViewModel.GetPlaceStates.Loading -> {
                Box(
                    modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    1
                    CircularProgressIndicator()
                }

            }
            //Search Result
            is SearchScreenViewModel.GetPlaceStates.Success -> {
                val places = placeStates.data
                LazyColumn(
                    modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    items(places) { place ->
                        SearchResultItem(
                            place = place,
                            onClick = {
                                selectPlace = place
                                addToRecent(place)
                            }
                        )
                    }
                }

                selectPlace?.let { it ->
                    CancelTrackingSheet(
                        modifier = modifier,
                        onDismiss = {
                            selectPlace = null
                        },
                        name = it.name,
                    ) { navigateToMapScreen(it) }
                }

            }
        }

    }


}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelTrackingSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    name: String,
    navigateToHomeScreen: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ready for your trip?",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We'll alert you before your stop."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("📍 $name ")


            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = modifier
                        .weight(1F)
                        .padding(end = 16.dp),
                    border = BorderStroke(1.dp, color = colorResource(R.color.Blue)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.white)
                    ),
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Cancel", color = colorResource(R.color.Blue))
                }
                Button(
                    modifier = modifier.weight(2F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.Blue)
                    ),
                    onClick = {
                        onDismiss()
                        navigateToHomeScreen()
                    }
                ) {
                    Text("Set Destination & Alarm")
                }
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentPlaceItem(
    place: PlaceModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val parts = place.name.split(",", limit = 2)

    val placeName = parts[0].trim()
    val address = parts.getOrNull(1)?.trim().orEmpty()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onClick() }
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = placeName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (address.isNotEmpty()) {
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    place: PlaceModel,
    onClick: () -> Unit,
) {
    val parts = place.name.split(",", limit = 2)

    val placeName = parts[0].trim()
    val address = parts.getOrNull(1)?.trim().orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = placeName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (address.isNotEmpty()) {
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NoRecentSearchCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No recent searches",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your recent places will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoSearchResultsCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Can't find what you're looking for?",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Try searching with a different name or address.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun NoSearchResultCardPrev() {
    NoSearchResultsCard()
}

@Preview
@Composable
private fun NoRecentSearchCardPrev() {
    NoRecentSearchCard()
}

@Preview
@Composable
private fun SearchResultItemPrev() {
    SearchResultItem(
        place = PlaceModel(
            id = "1",
            name = "Yangon",
            lat = 12121.0,
            lon = 21212.0
        )
    ) { }
}

@Preview
@Composable
private fun RecentPlaceItemPrev() {
    RecentPlaceItem(
        place = PlaceModel(
            id = "1",
            name = "Yangon",
            lat = 12121.0,
            lon = 21212.0
        ), onClick = {},
        onDelete = {})
}

@Preview
@Composable
private fun BottomSheetPrev() {
    CancelTrackingSheet(onDismiss = {}, name = "Siam Pragon", navigateToHomeScreen = {})
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
        search = {},
        navigateToMapScreen = {},
        recentPlace = listOf(),
        addToRecent = {},
        deleteRecent = {}
    )
}