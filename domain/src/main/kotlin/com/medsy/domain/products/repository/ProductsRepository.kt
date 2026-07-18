package com.medsy.domain.products.repository

import com.medsy.domain.products.model.Product
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        size: Int,
        sort: String
    ): Flow<MedsyResult<List<Product>, MedsyError.Remote>>
}
