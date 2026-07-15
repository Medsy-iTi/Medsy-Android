package com.medsy.presentation.productdetails

sealed interface ProductDetailsEvent {
    data object NavigateNext : ProductDetailsEvent
}
