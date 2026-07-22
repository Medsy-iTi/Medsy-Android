package com.medsy.domain.search.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.search.model.SearchProductsPage
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: List<String>?,
        categoryId: Int? = null
    ): MedsyResult<SearchProductsPage, MedsyError.Remote> {
        return repository.getProducts(page, size, sort, categoryId)
    }
}
