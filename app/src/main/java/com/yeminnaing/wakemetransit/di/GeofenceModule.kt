package com.yeminnaing.wakemetransit.di

import com.yeminnaing.wakemetransit.core.NotificationHelper
import com.yeminnaing.wakemetransit.core.location.LocationTracker
import com.yeminnaing.wakemetransit.core.location.LocationTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class GeofenceModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        impl: LocationTrackerImpl
    ): LocationTracker

}


