package com.medsy.data.remote.api

import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<ApiResponse<CategoriesDataDto>>
}
