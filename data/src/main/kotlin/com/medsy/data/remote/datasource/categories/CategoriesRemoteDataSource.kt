package com.medsy.data.remote.datasource.categories

import com.medsy.data.remote.dtos.categories.CategoriesDataDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface CategoriesRemoteDataSource {
    suspend fun getCategories(
        page: Int,
        size: Int,
        sort: String,
    ): MedsyResult<CategoriesDataDto, MedsyError.Remote>
}


