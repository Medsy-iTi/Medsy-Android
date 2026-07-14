package com.medsy.presentation.search

data class SearchState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)
