package com.medsy.data.aichat.remote

import com.medsy.data.cart.remote.CartInteractionsDto
import com.medsy.data.prescription.remote.AiInterceptor
import com.medsy.data.remote.network.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AiService {

    @POST("api/v1/ai/chat/messages")
    suspend fun sendChatMessage(
        @Body request: ChatMessageRequestDto,
    ): Response<ApiResponse<ChatMessageResponseDto>>

    @Multipart
    @Headers("${AiInterceptor.AI_KEY_FLAG}: true")
    @POST("api/v1/ai/chat/messages/image")
    suspend fun sendChatImageMessage(
        @Part image: MultipartBody.Part,
        @Part("message") message: RequestBody?,
    ): Response<ApiResponse<ChatMessageResponseDto>>

    @GET("api/v1/ai/chat/history")
    suspend fun getChatHistory(): Response<ApiResponse<ChatHistoryResponseDto>>

    @DELETE("api/v1/ai/chat/history")
    suspend fun deleteChatHistory(): Response<ApiResponse<ChatHistoryResponseDto>>

    @GET("api/v1/cart/interactions")
    suspend fun getCartInteractions(): Response<ApiResponse<CartInteractionsDto>>
}