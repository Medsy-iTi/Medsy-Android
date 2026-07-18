package com.medsy.data.remote.datasource.products

import com.medsy.data.remote.dtos.products.ProductsDataDto
import com.medsy.data.remote.network.ApiResult

interface ProductsRemoteDataSource {
    suspend fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<ProductsDataDto>
}
