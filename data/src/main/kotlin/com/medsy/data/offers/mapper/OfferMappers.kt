package com.medsy.data.offers.mapper

import com.medsy.data.offers.remote.RequestResultDto
import com.medsy.data.offers.remote.RequestResultItemDto
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultItem

fun RequestResultDto.toDomain(): RequestResult = RequestResult(
    items = medicineRequestResultItemList.map { it.toDomain() },
    totalPrice = totalPrice,
)

fun RequestResultItemDto.toDomain(): RequestResultItem = RequestResultItem(
    requestItemId = requestItemId,
    productId = productId,
    unitPrice = unitPrice,
    isAlternative = alternative,
    isAvailable = available,
    product = product?.toDomain(),
    alternatives = alternatives.map { it.toDomain() }
)

fun com.medsy.data.offers.remote.ResultProductDto.toDomain(): com.medsy.domain.offers.model.ResultProduct = com.medsy.domain.offers.model.ResultProduct(
    id = id,
    name = name,
    productName = productName,
    strength = strength,
    packSize = packSize,
    form = form,
    price = price,
    scientificName = scientificName,
    company = company,
    route = route,
    description = description,
    imageUrl = imageUrl
)
