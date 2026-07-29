package com.medsy.data.search.di

import com.medsy.data.remote.datasource.search.SearchRemoteDataSource
import com.medsy.data.remote.datasource.search.SearchRemoteDataSourceImpl
import com.medsy.data.search.repository.SearchRepositoryImpl
import com.medsy.domain.search.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchDataModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindSearchRemoteDataSource(
        impl: SearchRemoteDataSourceImpl
    ): SearchRemoteDataSource
}
