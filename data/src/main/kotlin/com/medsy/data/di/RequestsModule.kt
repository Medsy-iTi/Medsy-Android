package com.medsy.data.di

import com.medsy.data.requests.repository.ActiveRequestRepositoryImpl
import com.medsy.domain.requests.repository.ActiveRequestRepository
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
    abstract fun bindActiveRequestRepository(
        impl: ActiveRequestRepositoryImpl
    ): ActiveRequestRepository

    @Binds
    @Singleton
    abstract fun bindRequestsRepository(
        impl: com.medsy.data.requests.repository.RequestsRepositoryImpl
    ): com.medsy.domain.requests.repository.RequestsRepository
}
