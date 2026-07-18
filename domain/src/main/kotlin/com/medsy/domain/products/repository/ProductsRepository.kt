package com.medsy.domain.products.repository

import com.medsy.domain.products.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        size: Int,
        sort: String
    ): Flow<Result<List<Product>>>
}
