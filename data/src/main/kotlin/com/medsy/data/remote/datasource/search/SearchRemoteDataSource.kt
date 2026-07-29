package com.medsy.data.remote.datasource.search

import com.medsy.data.search.dto.ProductsPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface SearchRemoteDataSource {
    suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>?,
        categoryId: Int? = null
    ): MedsyResult<ProductsPageDto, MedsyError.Remote>

    suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<ProductsPageDto, MedsyError.Remote>
}
