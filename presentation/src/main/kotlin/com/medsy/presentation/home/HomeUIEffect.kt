package com.medsy.presentation.home

sealed interface HomeUIEffect {
    object NavigateToSearch : HomeUIEffect
    object NavigateToNotifications : HomeUIEffect
    object NavigateToAddressSelection : HomeUIEffect
    object NavigateToUploadPrescription : HomeUIEffect
    object NavigateToCategories : HomeUIEffect
    data class NavigateToCategory(val categoryId: String, val categoryName: String) : HomeUIEffect
}
