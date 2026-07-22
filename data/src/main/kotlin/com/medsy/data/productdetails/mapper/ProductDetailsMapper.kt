package com.medsy.data.productdetails.mapper

import com.medsy.data.productdetails.remote.ProductDetailsDto
import com.medsy.domain.productdetails.model.ProductDetails

fun ProductDetailsDto.toDomain(): ProductDetails {
    return ProductDetails(
        id = id,
        name = name,
        productName = productName,
        strength = strength,
        packSize = packSize,
        form = form,
        price = price,
        scientificName = scientificName,
        scientificCategory = scientificCategory,
        categoryId = categoryId,
        consumerCategory = consumerCategory,
        company = company,
        route = route,
        description = description,
        imageUrl = imageUrl
    )
}