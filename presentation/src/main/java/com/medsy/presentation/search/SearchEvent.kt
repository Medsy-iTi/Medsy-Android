package com.medsy.presentation.search

sealed interface SearchEvent {
    data object NavigateNext : SearchEvent
}
