package com.medsy.data.remote.datasource.search

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.search.dto.ProductsPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRemoteDataSource {

    override suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>?,
        categoryId: Int?
    ): MedsyResult<ProductsPageDto, MedsyError.Remote> = safeApiCall {
        apiService.getProducts(page, size, sort, categoryId)
    }

    override suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<ProductsPageDto, MedsyError.Remote> = safeApiCall {
        apiService.searchProducts(keyword, page, size, sort)
    }
}
