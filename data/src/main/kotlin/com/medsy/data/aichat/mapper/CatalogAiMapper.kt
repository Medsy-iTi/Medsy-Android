package com.medsy.data.aichat.mapper

import com.medsy.data.aichat.remote.CatalogAnswerDto
import com.medsy.data.aichat.remote.CatalogSourceDto
import com.medsy.domain.aichat.model.AiCatalogProduct
import com.medsy.domain.aichat.model.AiChatContent

fun CatalogAnswerDto.toDomain(): AiChatContent.AssistantResponse =
    AiChatContent.AssistantResponse(
        answer = answer?.trim().orEmpty(),
        products = sources.orEmpty().mapNotNull(CatalogSourceDto::toDomain),
    )

private fun CatalogSourceDto.toDomain(): AiCatalogProduct? {
    val sourceProduct = product ?: return null
    val productId = sourceProduct.id
        ?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }
        ?.toInt()
        ?: return null
    val displayName = sourceProduct.productName
        ?.takeIf(String::isNotBlank)
        ?: sourceProduct.name?.takeIf(String::isNotBlank)
        ?: return null

    return AiCatalogProduct(
        productId = productId,
        name = displayName,
        productName = sourceProduct.name ?: "",
        scientificName = sourceProduct.scientificName?.takeIf(String::isNotBlank),
        priceEgp = sourceProduct.price,
        strength = sourceProduct.strength?.takeIf(String::isNotBlank),
        packSize = sourceProduct.packSize?.takeIf(String::isNotBlank),
        form = sourceProduct.form?.takeIf(String::isNotBlank),
        company = sourceProduct.company?.takeIf(String::isNotBlank),
        route = sourceProduct.route?.takeIf(String::isNotBlank),
        description = sourceProduct.description?.takeIf(String::isNotBlank),
        imageUrl = sourceProduct.imageUrl?.takeIf(String::isNotBlank),
    )
}
