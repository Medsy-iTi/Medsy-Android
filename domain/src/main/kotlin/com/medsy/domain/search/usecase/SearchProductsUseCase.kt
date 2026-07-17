package com.medsy.domain.search.usecase

import com.medsy.domain.search.model.SearchProductsPage
import com.medsy.domain.search.repository.SearchRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<SearchProductsPage> {
        return repository.getProducts(page, size, sort)
    }
}
