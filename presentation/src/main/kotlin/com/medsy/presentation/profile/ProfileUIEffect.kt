package com.medsy.presentation.profile

sealed interface ProfileUIEffect {
    data object NavigateNext : ProfileUIEffect
}
