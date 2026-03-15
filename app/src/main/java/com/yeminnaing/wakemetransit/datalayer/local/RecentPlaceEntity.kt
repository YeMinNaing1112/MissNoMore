package com.yeminnaing.wakemetransit.datalayer.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_place")
data class RecentPlaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
)