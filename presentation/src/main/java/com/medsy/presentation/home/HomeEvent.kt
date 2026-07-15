package com.medsy.presentation.home

sealed interface HomeEvent {
    data object NavigateNext : HomeEvent
}
