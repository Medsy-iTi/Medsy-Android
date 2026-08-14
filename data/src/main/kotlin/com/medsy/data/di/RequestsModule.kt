package com.medsy.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RequestsModule {

    @Binds
    @Singleton
    abstract fun bindRequestsRepository(
        impl: com.medsy.data.requests.repository.RequestsRepositoryImpl
    ): com.medsy.domain.requests.repository.RequestsRepository
}
