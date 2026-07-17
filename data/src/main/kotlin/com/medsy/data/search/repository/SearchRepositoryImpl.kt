package com.medsy.data.search.repository

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.ApiError
import com.medsy.data.remote.network.ApiException
import com.medsy.data.remote.network.ApiResult
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.search.mapper.toDomain
import com.medsy.data.search.remote.ProductsPageDto
import com.medsy.domain.search.model.SearchProductsPage
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {

    override suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<SearchProductsPage> {
        return try {
            val apiResult = safeApiCall {
                apiService.getProducts(page, size, sort)
            }

            when (apiResult) {
                is ApiResult.Success -> {
                    val data = apiResult.data
                    if (data != null) {
                        Result.success(data.toDomain())
                    } else {
                        Result.failure(ApiException(ApiError.EmptyResponse))
                    }
                }
                is ApiResult.Error -> {
                    Result.failure(ApiException(apiResult.error))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(ApiException(ApiError.Unknown(e.message)))
        }
    }
}