package com.medsy.data.payment.di


import com.medsy.data.payment.repsitory.PaymentRepositoryImpl
import com.medsy.domain.payment.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        implementation: PaymentRepositoryImpl,
    ): PaymentRepository
}