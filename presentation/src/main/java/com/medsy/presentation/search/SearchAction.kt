package com.medsy.presentation.search

sealed interface SearchAction {
    data object OnNextClick : SearchAction
}
