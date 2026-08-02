package com.medsy.data.offers.mapper

import com.medsy.data.offers.remote.OfferDto
import com.medsy.data.offers.remote.OfferItemDto
import com.medsy.data.offers.remote.OffersPageDto
import com.medsy.data.offers.remote.RequestResultDto
import com.medsy.data.offers.remote.RequestResultItemDto
import com.medsy.domain.offers.model.Offer
import com.medsy.domain.offers.model.OfferItem
import com.medsy.domain.offers.model.OffersPage
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultItem

fun OfferDto.toDomain(): Offer = Offer(
    id = id,
    requestId = requestId,
    pharmacyId = pharmacyId,
    pharmacistId = pharmacistId,
    pharmacyName = pharmacyName,
    pharmacistName = pharmacistName,
    status = status,
    distanceKm = distanceKm,
    items = items.map { it.toDomain() }
)

fun OfferItemDto.toDomain(): OfferItem = OfferItem(
    id = id,
    requestItemId = requestItemId,
    productId = productId
)

fun OffersPageDto.toDomain(): OffersPage = OffersPage(
    content = content.map { it.toDomain() },
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages,
    last = last
)

fun RequestResultDto.toDomain(): RequestResult = RequestResult(
    items = medicineRequestResultItemList.map { it.toDomain() },
    totalPrice = totalPrice,
)

fun RequestResultItemDto.toDomain(): RequestResultItem = RequestResultItem(
    requestItemId = requestItemId,
    productId = productId,
    productName = productName,
    imageUrl = imageUrl,
    unitPrice = unitPrice,
    isAlternative = alternative,
    isAvailable = available,
)
