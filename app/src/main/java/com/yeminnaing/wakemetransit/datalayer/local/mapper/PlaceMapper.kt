package com.yeminnaing.wakemetransit.datalayer.local.mapper

import com.yeminnaing.wakemetransit.datalayer.local.RecentPlaceEntity
import com.yeminnaing.wakemetransit.domainlayer.model.PlaceModel

fun RecentPlaceEntity.toPlaceModel(): PlaceModel {
    return PlaceModel(
        name = name,
        id = id,
        lat = lat,
        lon = lon
    )
}