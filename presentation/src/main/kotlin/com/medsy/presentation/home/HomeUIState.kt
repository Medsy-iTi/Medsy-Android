package com.medsy.presentation.home

data class HomeUIState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val notificationCount: Int = 0,
    val deliveryAddress: String = "",
    val banners: List<PromoBannerUi> = emptyList(),
    val currentBannerIndex: Int = 0,
    val categories: List<CategoryUi> = emptyList(),
    val activeSearchStatuses: List<ActiveSearchStatus> = emptyList()
)

sealed interface ActiveSearchStatus {
    val requestId: Long
    val remainingTimeSeconds: Int
    
    data class Searching(
        override val requestId: Long,
        override val remainingTimeSeconds: Int
    ) : ActiveSearchStatus
    
    data class FirstOfferArrived(
        override val requestId: Long,
        override val remainingTimeSeconds: Int,
        val minPrice: Int,
        val totalOffers: Int = 1,
        val foundCount: Int = 0,
        val totalCount: Int = 0
    ) : ActiveSearchStatus
    
    data class MultipleOffersArrived(
        override val requestId: Long,
        override val remainingTimeSeconds: Int,
        val minPrice: Int,
        val totalOffers: Int,
        val foundCount: Int = 0,
        val totalCount: Int = 0
    ) : ActiveSearchStatus
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
    val name: String
)
