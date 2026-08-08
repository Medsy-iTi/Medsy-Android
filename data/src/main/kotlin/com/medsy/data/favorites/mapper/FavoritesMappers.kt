package com.medsy.data.favorites.mapper

import com.medsy.data.favorites.dto.FavoriteProductDto
import com.medsy.data.local.database.entity.FavoriteProductEntity
import com.medsy.domain.favorites.model.FavoriteProduct

fun FavoriteProductDto.toDomain(): FavoriteProduct = FavoriteProduct(
    id = id,
    name = name,
    arabicName = arabicName,
    scientificName = scientificName,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    categoryName = categoryName,
    company = company,
    route = route
)

fun FavoriteProduct.toDto(): FavoriteProductDto = FavoriteProductDto(
    id = id,
    name = name,
    arabicName = arabicName,
    scientificName = scientificName,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    categoryName = categoryName,
    company = company,
    route = route
)

fun FavoriteProductEntity.toDto(): FavoriteProductDto = FavoriteProductDto(
    id = id,
    name = name,
    arabicName = arabicName,
    scientificName = scientificName,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    categoryName = categoryName,
    company = company,
    route = route
)

fun FavoriteProductDto.toEntity(userId: Long): FavoriteProductEntity = FavoriteProductEntity(
    userId = userId,
    id = id,
    name = name,
    arabicName = arabicName,
    scientificName = scientificName,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    categoryName = categoryName,
    company = company,
    route = route
)
