package com.medsy.presentation.cart

sealed interface CartAction {
    data object OnNextClick : CartAction
}
