package com.medsy.domain.offers.model

import com.medsy.domain.common.model.PaymentMethod

data class RequestResult(
    val items: List<RequestResultItem>,
    val totalPrice: Double,
    val paymentMethod: PaymentMethod,
)

data class RequestResultItem(
    val requestItemId: Long,
    val productId: Long?,
    val unitPrice: Double,
    val isAlternative: Boolean,
    val isAvailable: Boolean,
    val product: ResultProduct?,
    val alternatives: List<ResultProduct> = emptyList(),
    val pharmacy: ResultPharmacy? = null,
)

data class ResultPharmacy(
    val id: Long,
    val name: String,
)

data class ResultProduct(
    val id: Long,
    val name: String,
    val productName: String,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val price: Double,
    val scientificName: String?,
    val company: String?,
    val route: String?,
    val description: String?,
    val imageUrl: String?
)

data class SelectedRequestItem(
    val requestItemId: Long,
    val productId: Long
)

sealed interface RequestResultEvent {
    data class Snapshot(val result: RequestResult) : RequestResultEvent

    data class ItemsUpdated(
        val requestId: Long,
        val items: List<RequestItemUpdate>,
    ) : RequestResultEvent

    data class Closed(val reason: StreamCloseReason) : RequestResultEvent
}

data class RequestItemUpdate(
    val requestItemId: Long,
    val status: RequestItemAvailability,
    val product: ResultProduct?,
)

enum class RequestItemAvailability {
    NOT_FOUND,
    ALTERNATIVE_FOUND,
    FOUND,
    UNKNOWN,
}

enum class StreamCloseReason {
    CONFIRMED,
    TIMEOUT,
    ERROR,
    UNKNOWN,
}
