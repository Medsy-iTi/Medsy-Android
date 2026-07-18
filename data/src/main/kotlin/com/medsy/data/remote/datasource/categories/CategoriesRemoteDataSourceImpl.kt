package com.medsy.data.remote.datasource.categories

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class CategoriesRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : CategoriesRemoteDataSource {
    override suspend fun getCategories(
        page: Int,
        size: Int,
        sort: String
    ): MedsyResult<CategoriesDataDto, MedsyError.Remote> {
        return safeApiCall { apiService.getCategories(page, size, sort) }
    }
}
