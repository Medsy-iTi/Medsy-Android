package com.medsy.data.remote.dtos.categories

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDto(
    val id: Int,
    val name: String
)


