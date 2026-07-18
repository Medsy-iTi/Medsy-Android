package com.medsy.data.di

import com.medsy.data.remote.datasource.products.ProductsRemoteDataSource
import com.medsy.data.remote.datasource.products.ProductsRemoteDataSourceImpl
import com.medsy.data.repository.ProductsRepositoryImpl
import com.medsy.domain.products.repository.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsModule {

    @Binds
    @Singleton
    abstract fun bindProductsRemoteDataSource(
        productsRemoteDataSourceImpl: ProductsRemoteDataSourceImpl
    ): ProductsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ): ProductsRepository
}
