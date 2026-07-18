package com.medsy.data.remote.auth.api

import com.medsy.data.remote.auth.dto.AuthDataDto
import com.medsy.data.remote.auth.dto.LoginRequestDto
import com.medsy.data.remote.auth.dto.RefreshRequestDto
import com.medsy.data.remote.auth.dto.RegisterRequestDto
import com.medsy.data.remote.auth.dto.VerifyOtpRequestDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<ApiResponse<Any>>

    @POST("api/v1/auth/verify")
    suspend fun verify(@Body body: VerifyOtpRequestDto): Response<ApiResponse<AuthDataDto>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ApiResponse<AuthDataDto>>

    @retrofit2.http.Headers("No-Auth: true")
    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): Response<ApiResponse<AuthDataDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body body: RefreshRequestDto): Response<ApiResponse<Any>>
}
