package com.medsy.data.remote.mapper

import com.medsy.data.remote.dtos.categories.CategoryDto
import com.medsy.domain.categories.model.Category


fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name
    )
}
