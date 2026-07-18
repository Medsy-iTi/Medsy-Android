package com.medsy.data.repository

import com.medsy.data.remote.datasource.products.ProductsRemoteDataSource
import com.medsy.data.remote.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.products.model.Product
import com.medsy.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val productsRemoteDataSource: ProductsRemoteDataSource
) : ProductsRepository {
    override fun getProductsByCategory(
        categoryId: Int,
        page: Int,
        size: Int,
        sort: String
    ): Flow<MedsyResult<List<Product>, MedsyError.Remote>> = flow {
        emit(
            productsRemoteDataSource.getProductsByCategory(categoryId, page, size, sort)
                .map { response -> response.content.map { it.toDomain() } }
        )
    }
}
