package com.medsy.domain.offers.model

data class RequestResult(
    val items: List<RequestResultItem>,
    val totalPrice: Double,
    val paymentMethod: String?,
)

data class RequestResultItem(
    val requestItemId: Long,
    val productId: Long?,
    val unitPrice: Double,
    val isAlternative: Boolean,
    val isAvailable: Boolean,
    val product: ResultProduct?,
    val alternatives: List<ResultProduct> = emptyList()
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
