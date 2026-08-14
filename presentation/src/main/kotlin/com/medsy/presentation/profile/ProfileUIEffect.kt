package com.medsy.presentation.profile

sealed interface ProfileUIEffect {
    data class NavigateToPersonalDetails(
        val startInEditMode: Boolean,
    ) : ProfileUIEffect
    data object NavigateToLogin : ProfileUIEffect
    data object NavigateToFavorites : ProfileUIEffect
    data object NavigateToReminders : ProfileUIEffect
}
