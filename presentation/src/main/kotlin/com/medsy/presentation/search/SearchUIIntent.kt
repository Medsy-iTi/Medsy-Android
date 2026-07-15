package com.medsy.presentation.search

sealed interface SearchUIIntent {
    data object OnNextClick : SearchUIIntent
}
