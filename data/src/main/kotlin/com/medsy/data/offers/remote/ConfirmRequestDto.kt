package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfirmRequestDto(
    val selectedItems: List<SelectedItemDto>,
)

@JsonClass(generateAdapter = true)
data class SelectedItemDto(
    val requestItemId: Long,
    val productId: Long,
)
