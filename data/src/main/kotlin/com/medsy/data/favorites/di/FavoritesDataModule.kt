package com.medsy.data.favorites.di

import com.medsy.data.favorites.repository.FavoritesRepositoryImpl
import com.medsy.data.local.datasource.favorites.FavoritesLocalDataSource
import com.medsy.data.local.datasource.favorites.FavoritesLocalDataSourceImpl
import com.medsy.domain.favorites.repository.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesDataModule {

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesLocalDataSource(
        impl: FavoritesLocalDataSourceImpl
    ): FavoritesLocalDataSource
}
