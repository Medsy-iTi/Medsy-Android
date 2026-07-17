package com.medsy.data.remote.datasource.categories

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.data.remote.network.ApiResult
import com.medsy.data.remote.network.safeApiCall
import javax.inject.Inject

class CategoriesRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : CategoriesRemoteDataSource {
    override suspend fun getCategories(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<CategoriesDataDto> {
        return safeApiCall { apiService.getCategories(page, size, sort) }
    }
}