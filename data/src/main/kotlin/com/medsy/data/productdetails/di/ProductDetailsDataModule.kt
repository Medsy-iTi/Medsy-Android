package com.medsy.data.productdetails.di

import com.medsy.data.productdetails.repository.ProductDetailsRepositoryImpl
import com.medsy.domain.productdetails.repository.ProductDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDetailsDataModule {

    @Binds
    @Singleton
    abstract fun bindProductDetailsRepository(
        impl: ProductDetailsRepositoryImpl
    ): ProductDetailsRepository
}
