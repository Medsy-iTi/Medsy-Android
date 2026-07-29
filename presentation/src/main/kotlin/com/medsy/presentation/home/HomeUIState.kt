package com.medsy.presentation.home

data class HomeUIState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val notificationCount: Int = 0,
    val deliveryAddress: String = "",
    val banners: List<PromoBannerUi> = emptyList(),
    val currentBannerIndex: Int = 0,
    val categories: List<CategoryUi> = emptyList(),
    val activeSearchStatus: ActiveSearchStatus = ActiveSearchStatus.Idle
)

sealed interface ActiveSearchStatus {
    data object Idle : ActiveSearchStatus
    
    data class Searching(
        val stage: Int,
        val elapsedTime: Int
    ) : ActiveSearchStatus
    
    data class FirstOfferArrived(
        val elapsedTime: Int,
        val minPrice: Int,
        val totalOffers: Int = 1
    ) : ActiveSearchStatus
    
    data class MultipleOffersArrived(
        val elapsedTime: Int,
        val minPrice: Int,
        val totalOffers: Int
    ) : ActiveSearchStatus
    
    data object SearchEndedNoOffers : ActiveSearchStatus
}

data class PromoBannerUi(
    val id: String,
    val titleRes: Int,
    val subtitleRes: Int,
    val extraTitleRes: Int,
    val imageRes: Int,
    val imageContentDescRes: Int
)

data class CategoryUi(
    val id: String,
    val name: String,
    val imageRes: Int? = null
)
