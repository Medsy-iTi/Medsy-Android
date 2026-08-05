package com.medsy.data.cart.mapper

import com.medsy.data.cart.remote.CartInteractionsDto
import com.medsy.data.cart.remote.InteractionWarningDto
import com.medsy.data.cart.remote.InvolvedProductDto
import com.medsy.domain.cart.model.InteractionProduct
import com.medsy.domain.cart.model.InteractionSeverity
import com.medsy.domain.cart.model.InteractionWarning

fun CartInteractionsDto.toDomain(): List<InteractionWarning> =
    warnings.orEmpty().mapNotNull(InteractionWarningDto::toDomain)

private fun InteractionWarningDto.toDomain(): InteractionWarning? {
    val warningTitle = title?.takeIf(String::isNotBlank) ?: return null
    return InteractionWarning(
        severity = when (severity?.trim()?.uppercase()) {
            "HIGH" -> InteractionSeverity.HIGH
            else -> InteractionSeverity.MODERATE
        },
        title = warningTitle,
        advice = advice?.trim().orEmpty(),
        involvedProducts = involvedProducts.orEmpty().mapNotNull(InvolvedProductDto::toDomain),
    )
}

private fun InvolvedProductDto.toDomain(): InteractionProduct? {
    val id = productId ?: return null
    return InteractionProduct(
        productId = id,
        productName = productName?.takeIf(String::isNotBlank) ?: return null,
        ingredient = ingredient.orEmpty(),
    )
}
