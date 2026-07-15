package com.medsy.presentation.productdetails

sealed interface ProductDetailsUIEffect {
    data object NavigateNext : ProductDetailsUIEffect
}
