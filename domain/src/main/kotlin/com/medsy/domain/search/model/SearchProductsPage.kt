package com.medsy.domain.search.model

data class SearchProductsPage(
    val content: List<SearchProduct>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val isLast: Boolean
)
