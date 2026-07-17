package com.medsy.data.remote.model.categories

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriesDataDto(
    val content: List<CategoryDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)
