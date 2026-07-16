package com.medsy.presentation.profile

sealed interface ProfileUIEffect {
    data object NavigateToPersonalDetails : ProfileUIEffect
    data object NavigateToLogin : ProfileUIEffect
}
