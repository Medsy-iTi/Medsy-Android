package com.medsy.presentation.profile

sealed interface ProfileAction {
    data object OnNextClick : ProfileAction
}
