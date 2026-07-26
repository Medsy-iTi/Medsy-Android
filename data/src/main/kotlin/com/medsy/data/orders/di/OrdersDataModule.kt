package com.medsy.data.orders.di

import com.medsy.data.orders.remote.OrdersRemoteDataSource
import com.medsy.data.orders.remote.OrdersRemoteDataSourceImpl
import com.medsy.data.orders.repository.OrdersRepositoryImpl
import com.medsy.domain.orders.repository.OrdersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersDataModule {

    @Binds
    @Singleton
    abstract fun bindOrdersRemoteDataSource(
        impl: OrdersRemoteDataSourceImpl
    ): OrdersRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindOrdersRepository(
        impl: OrdersRepositoryImpl
    ): OrdersRepository
}