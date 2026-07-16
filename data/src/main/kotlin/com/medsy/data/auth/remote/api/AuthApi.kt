package com.medsy.data.auth.remote.api

import com.medsy.data.auth.remote.dto.ApiResponseDto
import com.medsy.data.auth.remote.dto.AuthDataDto
import com.medsy.data.auth.remote.dto.LoginRequestDto
import com.medsy.data.auth.remote.dto.RefreshRequestDto
import com.medsy.data.auth.remote.dto.RegisterRequestDto
import com.medsy.data.auth.remote.dto.VerifyOtpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<ApiResponseDto<Unit>>

    @POST("api/v1/auth/verify")
    suspend fun verify(@Body body: VerifyOtpRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @Header("No-Auth")
    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): Response<ApiResponseDto<AuthDataDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body body: RefreshRequestDto): Response<ApiResponseDto<Unit>>
}
