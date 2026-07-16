package com.medsy.presentation.search

import com.medsy.presentation.R

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val filters: List<SearchFilterChipUi> = defaultSearchFilterChips(),
    val products: List<SearchProductUi> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
) {
    val resultsCount: Int get() = products.size
    val isEmpty: Boolean get() = !isLoading && products.isEmpty()
}
data class SearchFilterChipUi(
    val id: String,
    val labelRes: Int,
    val isSelected: Boolean = false,
    val hasLeadingIcon: Boolean = false,
)

enum class SearchFilterId { FILTER, TYPE, PRICE, MOST_RELEVANT }

fun defaultSearchFilterChips(selected: SearchFilterId = SearchFilterId.MOST_RELEVANT) = listOf(
    SearchFilterChipUi(
        id = SearchFilterId.FILTER.name,
        labelRes = R.string.search_filter_chip_filter,
        isSelected = selected == SearchFilterId.FILTER,
        hasLeadingIcon = true,
    ),
    SearchFilterChipUi(
        id = SearchFilterId.TYPE.name,
        labelRes = R.string.search_filter_chip_type,
        isSelected = selected == SearchFilterId.TYPE,
    ),
    SearchFilterChipUi(
        id = SearchFilterId.PRICE.name,
        labelRes = R.string.search_filter_chip_price,
        isSelected = selected == SearchFilterId.PRICE,
    ),
    SearchFilterChipUi(
        id = SearchFilterId.MOST_RELEVANT.name,
        labelRes = R.string.search_filter_chip_most_relevant,
        isSelected = selected == SearchFilterId.MOST_RELEVANT,
    ),
)

data class SearchProductUi(
    val id: String,
    val name: String,
    val subtitle: String,
    val priceEgp: Int,
)
