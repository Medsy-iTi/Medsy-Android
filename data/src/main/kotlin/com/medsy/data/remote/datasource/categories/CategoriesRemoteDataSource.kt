package com.medsy.data.remote.datasource.categories

import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.data.remote.network.ApiResult

interface CategoriesRemoteDataSource {
    suspend fun getCategories(page: Int, size: Int, sort: String): ApiResult<CategoriesDataDto>
}


