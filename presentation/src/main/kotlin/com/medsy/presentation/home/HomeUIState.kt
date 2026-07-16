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
    val discountTextRes: Int,
    val ctaTextRes: Int
)

data class CategoryUi(
    val id: String,
    val nameRes: Int,
    val iconType: CategoryIconType
)

enum class CategoryIconType {
    MEDICINE,
    VITAMINS,
    PERSONAL_CARE,
    MEDICAL_DEVICES,
    BABY_CARE,
    SKIN_CARE,
    HAIR_CARE,
    DAILY_ESSENTIALS,
    MORE
}
