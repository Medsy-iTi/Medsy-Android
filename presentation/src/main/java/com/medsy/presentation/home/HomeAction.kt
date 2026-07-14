package com.medsy.presentation.home

sealed interface HomeAction {
    data object OnNextClick : HomeAction
}
