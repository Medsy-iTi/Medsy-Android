package com.medsy.presentation.offers.mapper

import com.medsy.domain.offers.model.Offer
import com.medsy.domain.offers.model.RequestResultItem
import com.medsy.presentation.offers.model.OfferMedicine
import com.medsy.presentation.offers.model.OfferType
import com.medsy.presentation.offers.model.PharmacyOffer

fun Offer.toPharmacyOffer(resultItemMap: Map<Long, RequestResultItem>): PharmacyOffer {
    val medicines = items.map { offerItem ->
        val resultItem = resultItemMap[offerItem.requestItemId]
        OfferMedicine(
            id = offerItem.requestItemId.toString(),
            productId = offerItem.productId.toLong(),
            name = resultItem?.productName ?: "",
            packageInfo = "",
            price = resultItem?.unitPrice?.toInt() ?: 0,
            isAvailable = resultItem?.isAvailable ?: false,
            quantity = 1,
            imageUrl = resultItem?.imageUrl,
            isSubstitute = resultItem?.isAlternative ?: false,
            originalProductName = null,
        )
    }

    val availableCount = medicines.count { it.isAvailable }
    val type = if (availableCount == medicines.size) OfferType.FULL else OfferType.PARTIAL
    val totalPrice = medicines.filter { it.isAvailable }.sumOf { it.price * it.quantity }

    return PharmacyOffer(
        id = id.toString(),
        pharmacyName = pharmacyName ?: "",
        managerName = pharmacistName ?: "",
        price = totalPrice,
        type = type,
        medicines = medicines,
        pharmacistComment = null,
    )
}
