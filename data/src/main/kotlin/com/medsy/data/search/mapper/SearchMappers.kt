package com.medsy.data.search.mapper

import com.medsy.data.search.remote.ProductDto
import com.medsy.data.search.remote.ProductsPageDto
import com.medsy.domain.search.model.SearchProduct
import com.medsy.domain.search.model.SearchProductsPage

fun ProductDto.toDomain(): SearchProduct {
    return SearchProduct(
        id = id,
        name = name,
        arabicName = arabicName?:"",
        scientificName = scientificName,
        price = price,
        imageUrl = imageUrl,
        categoryId = categoryId,
        categoryName = categoryName,
        company = company,
        route = route
    )
}

fun ProductsPageDto.toDomain(): SearchProductsPage {
    return SearchProductsPage(
        content = content.map { it.toDomain() },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        isLast = last
    )
}
