package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfirmRequestDto(
    val selectedItems: List<SelectedRequestItemDto>
)

@JsonClass(generateAdapter = true)
data class SelectedRequestItemDto(
    val requestItemId: Long,
    val productId: Long
)
