package com.medsy.presentation.home

data class HomeState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)
