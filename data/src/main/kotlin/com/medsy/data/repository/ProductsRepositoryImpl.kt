package com.medsy.data.repository

import com.medsy.data.remote.datasource.products.ProductsRemoteDataSource
import com.medsy.data.remote.mapper.toDomain
import com.medsy.data.remote.network.ApiResult
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
    ): Flow<Result<List<Product>>> = flow {
        when (val response =
            productsRemoteDataSource.getProductsByCategory(categoryId, page, size, sort)) {
            is ApiResult.Success -> {
                val domainProducts = response.data?.content?.map { it.toDomain() } ?: emptyList()
                emit(Result.success(domainProducts))
            }

            is ApiResult.Error -> {
                emit(Result.failure(Exception(response.error.toString())))
            }
        }
    }
}
