package com.medsy.data.di

import com.medsy.data.offers.repository.OffersRepositoryImpl
import com.medsy.domain.offers.repository.OffersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OffersModule {
    @Binds
    @Singleton
    abstract fun bindOffersRepository(
        offersRepositoryImpl: OffersRepositoryImpl,
    ): OffersRepository
}
