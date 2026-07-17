package com.medsy.data.remote.api

import com.medsy.data.remote.network.ApiResponse
import com.medsy.data.search.remote.ProductsPageDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>?
    ): Response<ApiResponse<ProductsPageDto>>

}