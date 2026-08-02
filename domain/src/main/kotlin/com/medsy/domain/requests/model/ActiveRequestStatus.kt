package com.medsy.domain.requests.model

sealed interface ActiveRequestStatus {
    val requestId: Long
    val remainingTimeSeconds: Int

    data class Searching(
        override val requestId: Long,
        override val remainingTimeSeconds: Int
    ) : ActiveRequestStatus

    data class FirstOfferArrived(
        override val requestId: Long,
        override val remainingTimeSeconds: Int,
        val minPrice: Int,
        val foundCount: Int,
        val totalCount: Int
    ) : ActiveRequestStatus

    data class MultipleOffersArrived(
        override val requestId: Long,
        override val remainingTimeSeconds: Int,
        val minPrice: Int,
        val totalOffers: Int,
        val foundCount: Int,
        val totalCount: Int
    ) : ActiveRequestStatus
}
