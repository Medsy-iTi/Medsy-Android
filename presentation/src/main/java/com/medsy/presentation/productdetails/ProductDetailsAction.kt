package com.medsy.presentation.productdetails

sealed interface ProductDetailsAction {
    data object OnNextClick : ProductDetailsAction
}
