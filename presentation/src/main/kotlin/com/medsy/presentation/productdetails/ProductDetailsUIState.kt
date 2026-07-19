package com.medsy.presentation.productdetails

import com.medsy.presentation.productdetails.model.Product

data class ProductDetailsUIState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val selectedImageIndex: Int = 0,
    val isFavorite: Boolean = false,
    val isAddingToCart: Boolean = false,
    val errorMessage: String? = null,
)
