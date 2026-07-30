package com.medsy.data.search.repository

import com.medsy.data.remote.datasource.search.SearchRemoteDataSource
import com.medsy.data.search.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.search.model.SearchProductsPage
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: SearchRemoteDataSource
) : SearchRepository {

    override suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>?,
        categoryId: Int?
    ): MedsyResult<SearchProductsPage, MedsyError.Remote> =
        remoteDataSource.getProducts(page, size, sort, categoryId)
            .map { it.toDomain() }

    override suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<SearchProductsPage, MedsyError.Remote> =
        remoteDataSource.searchProducts(keyword, page, size, sort)
            .map { it.toDomain() }
}
