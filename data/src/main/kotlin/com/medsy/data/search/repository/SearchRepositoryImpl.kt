package com.medsy.data.search.repository

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.ApiError
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
                        // تحويل الـ DTO إلى Domain Model
                        Result.success(data.toDomain())
                    } else {
                        Result.failure(Exception("Empty response data"))
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = when (val error = apiResult.error) {
                        is ApiError.Server -> error.message
                        is ApiError.Unknown -> error.message ?: "Unknown server error"
                        is ApiError.NoInternet -> "No internet connection"
                        is ApiError.EmptyResponse -> "Empty response from server"
                    }
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}