package com.yeminnaing.wakemetransit.di

import android.content.Context
import androidx.room.Room
import com.yeminnaing.wakemetransit.datalayer.local.MissNoMoreDataBase
import com.yeminnaing.wakemetransit.datalayer.local.RecentPlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object DataBaseModuleP {

    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context,
    ): MissNoMoreDataBase {
        return Room.databaseBuilder(
            context,
            MissNoMoreDataBase::class.java,
            "MissNoMore_DB",
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecentPlaceDao(
        database: MissNoMoreDataBase,
    ): RecentPlaceDao {
        return database.recentPlaceDao()
    }
}