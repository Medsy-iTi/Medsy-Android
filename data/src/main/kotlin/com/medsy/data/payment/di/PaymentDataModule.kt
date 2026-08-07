package com.medsy.data.payment.di

import com.medsy.data.payment.repository.PaymentRepositoryImpl
import com.medsy.domain.payment.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentDataModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        implementation: PaymentRepositoryImpl,
    ): PaymentRepository
}