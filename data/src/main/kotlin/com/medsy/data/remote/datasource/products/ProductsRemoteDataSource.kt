package com.medsy.data.remote.datasource.products

import com.medsy.data.remote.dtos.products.ProductsDataDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface ProductsRemoteDataSource {
    suspend fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        size: Int,
        sort: String
    ): MedsyResult<ProductsDataDto, MedsyError.Remote>
}
