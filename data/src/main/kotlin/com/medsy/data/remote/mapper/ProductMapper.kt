package com.medsy.data.remote.mapper

import com.medsy.data.search.dto.ProductDto
import com.medsy.domain.products.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        scientificName = scientificName,
        price = price,
        imageUrl = imageUrl ?: "",
        categoryId = categoryId,
        categoryName = categoryName ?: "",
        company = company ?: "",
        route = route ?: ""
    )
}
