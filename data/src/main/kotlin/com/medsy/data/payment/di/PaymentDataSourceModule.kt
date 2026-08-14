package com.medsy.data.payment.di


import com.medsy.data.payment.remote.PaymentRemoteDataSource
import com.medsy.data.payment.remote.PaymentRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRemoteDataSource(
        implementation: PaymentRemoteDataSourceImpl,
    ): PaymentRemoteDataSource
}