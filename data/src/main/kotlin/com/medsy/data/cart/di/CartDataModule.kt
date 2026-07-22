package com.medsy.data.cart.di

import com.medsy.data.cart.repository.CartRepositoryImpl
import com.medsy.domain.cart.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartDataModule {
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        implementation: CartRepositoryImpl,
    ): CartRepository
}
