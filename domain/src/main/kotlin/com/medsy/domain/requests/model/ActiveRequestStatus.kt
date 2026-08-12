package com.medsy.domain.requests.model

data class ActiveRequestStatus(
    val requestId: Long,
    val remainingTimeSeconds: Int,
    val foundCount: Int,
    val totalCount: Int,
) {
    val hasAvailableProducts: Boolean
        get() = foundCount > 0
}
