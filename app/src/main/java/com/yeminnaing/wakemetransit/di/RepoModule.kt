package com.yeminnaing.wakemetransit.di

import com.yeminnaing.wakemetransit.datalayer.repositories.SearchRepoImpl
import com.yeminnaing.wakemetransit.domainlayer.repositorie.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindSearchRepoImpl(
        mSearchRepoImpl : SearchRepoImpl
    ): SearchRepository
}