package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestResultDto(
    val medicineRequestResultItemList: List<RequestResultItemDto>,
    val totalPrice: Double,
    val paymentMethod: String?,
)

@JsonClass(generateAdapter = true)
data class RequestResultItemDto(
    val requestItemId: Long,
    val productId: Long?,
    val unitPrice: Double,
    val alternative: Boolean,
    val available: Boolean,
    val product: RequestResultProductDto?,
    val alternatives: List<RequestResultProductDto> = emptyList(),
    val pharmacy: ResultPharmacyDto? = null,
)

@JsonClass(generateAdapter = true)
data class ResultPharmacyDto(
    val id: Long,
    val name: String,
)

@JsonClass(generateAdapter = true)
data class RequestResultProductDto(
    val id: Long,
    val name: String,
    val productName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val price: Double,
    val scientificName: String?,
    val company: String?,
    val route: String?,
    val description: String?,
    val imageUrl: String?,
)

@JsonClass(generateAdapter = true)
data class RequestResultUpdateDto(
    val requestId: Long,
    val updatedItems: List<RequestItemUpdateDto>,
)

@JsonClass(generateAdapter = true)
data class RequestItemUpdateDto(
    val requestItemId: Long,
    val status: String,
    val product: RequestResultProductDto?,
)

sealed interface RequestStreamEventDto {
    data class Snapshot(val result: RequestResultDto) : RequestStreamEventDto
    data class ItemsUpdated(val update: RequestResultUpdateDto) : RequestStreamEventDto
    data class Closed(val reason: String) : RequestStreamEventDto
}
