package com.medsy.data.di

import com.medsy.data.remote.datasource.categories.CategoriesRemoteDataSource
import com.medsy.data.remote.datasource.categories.CategoriesRemoteDataSourceImpl
import com.medsy.data.repository.CategoriesRepositoryImpl
import com.medsy.domain.categories.repository.CategoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoriesModule {

    @Binds
    @Singleton
    abstract fun bindCategoriesRemoteDataSource(
        categoriesRemoteDataSourceImpl: CategoriesRemoteDataSourceImpl
    ): CategoriesRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository
}
