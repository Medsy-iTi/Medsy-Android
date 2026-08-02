package com.medsy.data.offers.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfirmRequestDto(
    val selectedRequestItemIds: List<Long>
)
