package com.yeminnaing.wakemetransit.datalayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: RecentPlaceEntity)

    @Query("SELECT * FROM recent_place LIMIT 10")
    fun getRecentPlace(): Flow<List<RecentPlaceEntity>>
    @Query("DELETE FROM recent_place")
    suspend fun clearRecent()
}
