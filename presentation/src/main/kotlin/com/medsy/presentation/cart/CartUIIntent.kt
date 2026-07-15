package com.medsy.presentation.cart

sealed interface CartUIIntent {
    data object OnNextClick : CartUIIntent
}
