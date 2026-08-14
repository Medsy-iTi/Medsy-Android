package com.medsy.domain.orders.usecase

import com.medsy.domain.common.model.PaymentMethod
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.model.OrderNextAction
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.PaymentStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class DetermineOrderNextActionUseCase @Inject constructor() {
    operator fun invoke(
        order: MasterOrder,
        now: Instant = Instant.now(),
    ): OrderNextAction = when {
        order.fulfillmentMethod == null -> OrderNextAction.CHOOSE_FULFILLMENT
        order.paymentMethod == PaymentMethod.CARD &&
            order.orderStatus == OrderStatus.PENDING_PAYMENT &&
            order.paymentStatus in ACTIONABLE_PAYMENT_STATUSES &&
            order.paymentExpiresAt.toInstantOrNull()?.isAfter(now) == true -> OrderNextAction.PAY_CARD
        else -> OrderNextAction.VIEW_DETAILS
    }

    private companion object {
        val ACTIONABLE_PAYMENT_STATUSES = setOf(
            PaymentStatus.UNPAID,
            PaymentStatus.PENDING,
            PaymentStatus.FAILED,
            PaymentStatus.CANCELED,
        )
    }
}

private fun String?.toInstantOrNull(): Instant? {
    if (this == null) return null
    return runCatching { OffsetDateTime.parse(this).toInstant() }.getOrNull()
        ?: runCatching { Instant.parse(this) }.getOrNull()
        ?: runCatching { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC) }.getOrNull()
}
