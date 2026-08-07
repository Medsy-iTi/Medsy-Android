package com.medsy.data.payment.remote

import com.medsy.data.payment.dtos.CreatePaymentIntentRequestDto
import com.medsy.data.payment.dtos.CreatePaymentIntentResponseDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {

    @POST("api/v1/payments/create-intent")
    suspend fun createPaymentIntent(
        @Body request: CreatePaymentIntentRequestDto
    ): Response<ApiResponse<CreatePaymentIntentResponseDto>>
}