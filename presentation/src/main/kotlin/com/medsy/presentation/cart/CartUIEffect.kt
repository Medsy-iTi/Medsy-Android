package com.medsy.presentation.cart

sealed interface CartUIEffect {
    data object NavigateNext : CartUIEffect
}
