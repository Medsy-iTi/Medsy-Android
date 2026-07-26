package com.medsy.data.search.repository

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.search.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.search.model.SearchProductsPage
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {

    override suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>?,
        categoryId: Int?

    ): MedsyResult<SearchProductsPage, MedsyError.Remote> =
        safeApiCall {
            apiService.getProducts(page, size, sort,categoryId)
        }.map { it.toDomain() }

    override suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<SearchProductsPage, MedsyError.Remote> =
        safeApiCall {
            apiService.searchProducts(keyword, page, size, sort)
        }.map { it.toDomain() }
}
