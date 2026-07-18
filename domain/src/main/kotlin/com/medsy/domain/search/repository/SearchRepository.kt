package com.medsy.domain.search.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.search.model.SearchProductsPage

interface SearchRepository {
    suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<SearchProductsPage, MedsyError.Remote>
}
