package com.medsy.data.remote.api

import com.medsy.data.profile.remote.dto.UpdateCustomerProfileRequestDto
import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.data.remote.network.ApiResponse
import com.medsy.data.profile.remote.dto.CustomerDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query


interface ApiService {
    @GET("api/v1/categories")
    suspend fun getCategories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<ApiResponse<CategoriesDataDto>>


    @GET("api/v1/customers/me")
    suspend fun getCurrentCustomer(): Response<ApiResponse<CustomerDto>>

    @PUT("api/v1/customers/me")
    suspend fun updateCurrentCustomer(
        @Body request: UpdateCustomerProfileRequestDto,
    ): Response<ApiResponse<CustomerDto>>

}
