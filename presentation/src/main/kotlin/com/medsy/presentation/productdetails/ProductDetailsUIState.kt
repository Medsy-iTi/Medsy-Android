package com.medsy.presentation.productdetails

import androidx.annotation.StringRes
import com.medsy.presentation.productdetails.model.Product

data class ProductDetailsUIState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val selectedImageIndex: Int = 0,
    val isFavorite: Boolean = false,
    val isAddingToCart: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
