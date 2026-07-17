package com.medsy.presentation.home

data class HomeUIState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val notificationCount: Int = 0,
    val deliveryAddress: String = "",
    val banners: List<PromoBannerUi> = emptyList(),
    val currentBannerIndex: Int = 0,
    val categories: List<CategoryUi> = emptyList()
)

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
