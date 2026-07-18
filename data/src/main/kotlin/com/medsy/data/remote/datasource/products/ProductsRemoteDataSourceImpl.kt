package com.medsy.data.remote.datasource.products

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.dtos.products.ProductsDataDto
import com.medsy.data.remote.network.ApiResult
import com.medsy.data.remote.network.safeApiCall
import javax.inject.Inject

class ProductsRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : ProductsRemoteDataSource {
    override suspend fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<ProductsDataDto> {
        return safeApiCall { apiService.getProductsByCategory(categoryId, page, size, sort) }
    }
}
