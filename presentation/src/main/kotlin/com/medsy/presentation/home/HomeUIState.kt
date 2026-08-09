package com.medsy.presentation.home

import com.medsy.presentation.R

data class HomeUIState(
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val notificationCount: Int = 0,
    val deliveryAddress: String = "",
    val banners: List<PromoBannerUi> = PromoBannerUi.banners,
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
) {
    companion object {
        val banners = listOf(
            PromoBannerUi(
                id = "1",
                titleRes = R.string.home_promo_title_one,
                subtitleRes = R.string.home_promo_subtitle_one,
                extraTitleRes = R.string.home_promo_extratext_one,
                imageRes = R.drawable.banner1,
                imageContentDescRes = R.string.home_banner_image_desc_one
            ),
            PromoBannerUi(
                id = "2",
                titleRes = R.string.home_promo_title_two,
                subtitleRes = R.string.home_promo_subtitle_two,
                extraTitleRes = R.string.home_promo_extratext_two,
                imageRes = R.drawable.banner2,
                imageContentDescRes = R.string.home_banner_image_desc_two
            ),
            PromoBannerUi(
                id = "3",
                titleRes = R.string.home_promo_title_three,
                subtitleRes = R.string.home_promo_subtitle_three,
                extraTitleRes = R.string.home_promo_extratext_three,
                imageRes = R.drawable.banner3,
                imageContentDescRes = R.string.home_banner_image_desc_three
            ),
        )
    }
}

data class CategoryUi(
    val id: String,
    val name: String,
    val imageRes: Int? = null
)
