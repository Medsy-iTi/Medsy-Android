package com.medsy.presentation.search

sealed interface SearchUIEffect {
    data object NavigateNext : SearchUIEffect
}
