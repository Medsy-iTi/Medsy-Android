package com.medsy.data.productdetails.mapper

import com.medsy.data.productdetails.remote.ProductDetailsDto
import com.medsy.domain.productdetails.model.ProductDetails

fun ProductDetailsDto.toDomain(): ProductDetails {
    return ProductDetails(
        id = id,
        name = name,
        scientificName = scientificName,
        price = price,
        imageUrl = imageUrl,
        categoryId = categoryId,
        categoryName = categoryName,
        company = company,
        route = route
    )
}
