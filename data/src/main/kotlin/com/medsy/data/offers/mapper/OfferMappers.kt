package com.medsy.data.offers.mapper

import com.medsy.data.offers.remote.OfferDto
import com.medsy.data.offers.remote.OfferItemDto
import com.medsy.data.offers.remote.OffersPageDto
import com.medsy.domain.offers.model.Offer
import com.medsy.domain.offers.model.OfferItem
import com.medsy.domain.offers.model.OffersPage

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
