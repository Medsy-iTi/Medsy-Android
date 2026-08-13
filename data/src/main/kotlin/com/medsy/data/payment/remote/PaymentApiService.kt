
package com.medsy.data.payment.remote


import com.medsy.data.payment.remote.dto.CreatePaymentIntentRequestDto
import com.medsy.data.payment.remote.dto.CreatePaymentIntentResponseDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {

    @POST("api/v1/payments/create-intent")
    suspend fun createPaymentIntent(
        @Body request: CreatePaymentIntentRequestDto,
    ): Response<ApiResponse<CreatePaymentIntentResponseDto>>
}