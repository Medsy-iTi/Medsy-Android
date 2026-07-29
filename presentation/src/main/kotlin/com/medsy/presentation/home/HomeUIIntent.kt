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
    
    // Active Search Simulation Intents
    data object OnStartSearchSimulation : HomeUIIntent
    data class OnCancelSearchSimulation(val requestId: Long) : HomeUIIntent
    data class OnViewOffersClick(val requestId: Long) : HomeUIIntent
    object OnSearchWiderRangeClick : HomeUIIntent
    data class OnAddressResolved(val address: String) : HomeUIIntent
}
