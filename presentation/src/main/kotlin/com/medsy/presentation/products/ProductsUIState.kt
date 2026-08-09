package com.medsy.presentation.products

data class ProductsUIState(
    val categoryId: Int = -1,
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val products: List<ProductUi> = emptyList(),
    val filteredProducts: List<ProductUi> = emptyList(),
    val searchQuery: String = "",
    val favoriteProductIds: Set<Int> = emptySet(),
)

data class ProductUi(
    val id: Int,
    val name: String,
    val scientificName: String,
    val price: String,
    val imageUrl: String,
    val isFavorite: Boolean = false,
)
