package com.medsy.presentation.productdetails

sealed interface ProductDetailsUIIntent {
    data object BackClicked : ProductDetailsUIIntent
    data object ShareClicked : ProductDetailsUIIntent
    data object FavoriteClicked : ProductDetailsUIIntent
    data class ImagePageChanged(val index: Int) : ProductDetailsUIIntent
    data object AddToCartClicked : ProductDetailsUIIntent
    data object ConsultPharmacistClicked : ProductDetailsUIIntent
    data object RetryClicked : ProductDetailsUIIntent
}
