package com.yeminnaing.wakemetransit.datalayer.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RecentPlaceEntity::class],
    version = 1
)

abstract class MissNoMoreDataBase : RoomDatabase() {
    abstract fun recentPlaceDao(): RecentPlaceDao
}