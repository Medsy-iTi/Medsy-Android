package com.medsy.presentation.home

sealed interface HomeUIIntent {
    object OnSearchFieldClick : HomeUIIntent
    object OnSearchMedicineClick : HomeUIIntent
    object OnUploadPrescriptionClick : HomeUIIntent
    object OnNotificationClick : HomeUIIntent
    object OnAddressClick : HomeUIIntent
    object OnPromoClick : HomeUIIntent
    object OnViewAllCategoriesClick : HomeUIIntent
    data class OnCategoryClick(val categoryId: String) : HomeUIIntent
}
