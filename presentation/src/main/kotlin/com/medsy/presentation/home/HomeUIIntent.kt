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
    data class OnCancelSearchSimulation(val requestId: Long) : HomeUIIntent
    data class OnViewOffersClick(val requestId: Long) : HomeUIIntent
    object OnSearchWiderRangeClick : HomeUIIntent
    object RefreshData : HomeUIIntent
    object OnResume : HomeUIIntent
    data class LanguageChanged(val languageTag: String) : HomeUIIntent
}
