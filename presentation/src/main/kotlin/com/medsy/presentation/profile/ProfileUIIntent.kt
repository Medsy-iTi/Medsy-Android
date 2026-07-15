package com.medsy.presentation.profile

sealed interface ProfileUIIntent {
    data object OnNextClick : ProfileUIIntent
}
