package com.medsy.presentation.search

import com.medsy.domain.categories.model.Category
import com.medsy.presentation.R

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
    val products: List<SearchProductUi> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val selectedSort: SortOption = SortOption.NAME_ASC,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val isPriceBottomSheetOpen: Boolean = false,
    val isSortBottomSheetOpen: Boolean = false,
    val isCategoryBottomSheetOpen: Boolean = false,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLastPage: Boolean = true,
    val errorMessage: Int? = null,
) {
    val resultsCount: Int get() = products.size
    val isEmpty: Boolean get() = !isLoading && products.isEmpty()

    val filters: List<SearchFilterChipUi>
        get() = listOf(
            SearchFilterChipUi(
                id = SearchFilterId.SORT.name,
                labelRes = selectedSort.labelResId,
                isSelected = true,
                hasLeadingIcon = true
            ),
            SearchFilterChipUi(
                id = SearchFilterId.CATEGORY.name,
                label = selectedCategory?.name,
                labelRes = if (selectedCategory == null) R.string.search_category_all else null,
                isSelected = selectedCategory != null,
            )
        )
}

data class SearchFilterChipUi(
    val id: String,
    // Exactly one of labelRes/label should be set. label wins when both are
    // set, since it represents a dynamic value (e.g. a selected category
    // name) that a fixed string resource can't express.
    val labelRes: Int? = null,
    val label: String? = null,
    val isSelected: Boolean = false,
    val hasLeadingIcon: Boolean = false,
)

enum class SearchFilterId { SORT, PRICE, CATEGORY }

enum class PriceFilterOption(val minPrice: Double?, val maxPrice: Double?, val labelResId: Int) {
    ALL(null, null, R.string.search_price_all),
    UNDER_50(null, 50.0, R.string.search_price_under_50),
    FROM_50_TO_100(50.0, 100.0, R.string.search_price_50_to_100),
    FROM_100_TO_200(100.0, 200.0, R.string.search_price_100_to_200),
    OVER_200(200.0, null, R.string.search_price_over_200)
}

enum class SortOption(val apiValue: String, val labelResId: Int) {
    NAME_ASC("name,asc", R.string.search_sort_name_asc),
    NAME_DESC("name,desc", R.string.search_sort_name_desc),
    PRICE_ASC("price,asc", R.string.search_sort_price_asc),
    PRICE_DESC("price,desc", R.string.search_sort_price_desc),
    SCIENTIFIC_NAME_ASC("scientificName,asc", R.string.search_sort_scientific_name_asc),
    COMPANY_ASC("company,asc", R.string.search_sort_company_asc)
}

data class SearchProductUi(
    val id: String,
    val name: String,
    val subtitle: String,
    val priceEgp: Int,
    val imageUrl: String?
)
