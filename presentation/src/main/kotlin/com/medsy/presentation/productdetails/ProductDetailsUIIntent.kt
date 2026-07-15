package com.medsy.presentation.productdetails

sealed interface ProductDetailsUIIntent {
    data object OnNextClick : ProductDetailsUIIntent
}
