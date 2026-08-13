package com.medsy.data.payment.di

import com.medsy.data.payment.remote.PaymentApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    @Singleton
    fun providePaymentApiService(
        retrofit: Retrofit,
    ): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

}