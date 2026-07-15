package com.medsy.presentation.cart

sealed interface CartEvent {
    data object NavigateNext : CartEvent
}
