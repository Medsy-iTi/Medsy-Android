package com.medsy.data.aichat.mapper

import com.medsy.data.aichat.remote.ChatActionDto
import com.medsy.data.aichat.remote.ChatCategoryDto
import com.medsy.data.aichat.remote.ChatHistoryMessageDto
import com.medsy.data.aichat.remote.ChatMessageResponseDto
import com.medsy.data.aichat.remote.ChatProductDto
import com.medsy.data.aichat.remote.EmergencyNumberDto
import com.medsy.data.aichat.remote.ReminderDto
import com.medsy.domain.aichat.model.AiCatalogProduct
import com.medsy.domain.aichat.model.AiChatCategory
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatIntent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatMessageAction
import com.medsy.domain.aichat.model.AiChatSender
import com.medsy.domain.aichat.model.AiEmergencyNumber
import com.medsy.domain.aichat.model.AiEmergencyService
import com.medsy.domain.aichat.model.AiReminderInfo
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val ACTION_ADDED_TO_CART = "ADDED_TO_CART"
private const val ACTION_CREATE_REQUEST = "CREATE_REQUEST"

/** Backend marks image messages with this line inside the stored content. */
private val IMAGE_MARKER_REGEX = Regex("""\n?\[image][^\n]*""")

fun ChatMessageResponseDto.toDomain(): AiChatContent.AssistantMessage =
    AiChatContent.AssistantMessage(
        answer = answer?.trim().orEmpty(),
        intent = intent.toIntent(),
        products = products.orEmpty().mapNotNull(ChatProductDto::toDomain),
        doctorSpecializations = doctorSpecializations.orEmpty().filter(String::isNotBlank),
        emergencyNumbers = emergencyNumbers.orEmpty().mapNotNull(EmergencyNumberDto::toDomain),
        categories = categories.orEmpty().mapNotNull(ChatCategoryDto::toDomain),
        disclaimer = disclaimer?.takeIf(String::isNotBlank),
        action = action?.toDomain(),
        reminder = reminder?.toDomain(messageId),
    )

fun ChatHistoryMessageDto.toDomain(): AiChatMessage? {
    val messageId = id ?: return null
    return when (role?.uppercase()) {
        "USER" -> AiChatMessage(
            id = messageId,
            sender = AiChatSender.USER,
            content = AiChatContent.UserText(
                value = content.orEmpty().replace(IMAGE_MARKER_REGEX, "").trim(),
            ),
        )

        "ASSISTANT" -> AiChatMessage(
            id = messageId,
            sender = AiChatSender.ASSISTANT,
            content = AiChatContent.AssistantMessage(
                answer = content?.trim().orEmpty(),
                intent = intent.toIntent(),
                products = products.orEmpty().mapNotNull(ChatProductDto::toDomain),
                doctorSpecializations = doctorSpecializations.orEmpty().filter(String::isNotBlank),
                emergencyNumbers = emergencyNumbers.orEmpty()
                    .mapNotNull(EmergencyNumberDto::toDomain),
                categories = categories.orEmpty().mapNotNull(ChatCategoryDto::toDomain),
                // History never replays disclaimers or one-shot actions.
                disclaimer = null,
                action = null,
                reminder = null,
            ),
        )

        else -> null
    }
}

private fun ReminderDto.toDomain(sourceMessageId: Long?): AiReminderInfo? {
    val resolvedSourceMessageId = sourceMessageId ?: return null
    val resolvedMedicineName = medicineName?.trim()?.takeIf(String::isNotBlank) ?: return null
    val resolvedDuration = durationDays?.takeIf { it in 1..MAX_REMINDER_DURATION_DAYS }
        ?: return null
    val resolvedTimes = times.orEmpty()
        .mapNotNull { value ->
            val normalizedValue = value.trim()
            if (!REMINDER_TIME_REGEX.matches(normalizedValue)) return@mapNotNull null
            try {
                LocalTime.parse(normalizedValue, REMINDER_TIME_FORMAT)
            } catch (_: DateTimeParseException) {
                null
            }
        }
        .distinct()
        .sorted()
    if (resolvedTimes.isEmpty()) return null

    return AiReminderInfo(
        sourceMessageId = resolvedSourceMessageId,
        medicineName = resolvedMedicineName,
        times = resolvedTimes,
        durationDays = resolvedDuration,
    )
}

private fun String?.toIntent(): AiChatIntent {
    val value = this?.trim()?.uppercase() ?: return AiChatIntent.OTHER
    return AiChatIntent.entries.firstOrNull { it.name == value } ?: AiChatIntent.OTHER
}

private fun ChatActionDto.toDomain(): AiChatMessageAction? = when (type?.trim()?.uppercase()) {
    ACTION_ADDED_TO_CART -> AiChatMessageAction.AddedToCart(
        quantity = quantity?.takeIf { it > 0 } ?: 1,
        cartItemCount = cartItemCount,
    )

    ACTION_CREATE_REQUEST -> AiChatMessageAction.CreateRequest
    else -> null
}

private fun EmergencyNumberDto.toDomain(): AiEmergencyNumber? {
    val dialNumber = number?.takeIf(String::isNotBlank) ?: return null
    val emergencyService = when (service?.trim()?.uppercase()) {
        "AMBULANCE" -> AiEmergencyService.AMBULANCE
        "POLICE" -> AiEmergencyService.POLICE
        "FIRE" -> AiEmergencyService.FIRE
        else -> return null
    }
    return AiEmergencyNumber(service = emergencyService, number = dialNumber)
}

private fun ChatCategoryDto.toDomain(): AiChatCategory? {
    val categoryId = id
        ?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }
        ?.toInt()
        ?: return null
    val categoryName = name?.takeIf(String::isNotBlank) ?: return null
    return AiChatCategory(id = categoryId, name = categoryName)
}

private fun ChatProductDto.toDomain(): AiCatalogProduct? {
    val resolvedId = id
        ?.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }
        ?.toInt()
        ?: return null
    // Brand name first for the card title (strength/pack render separately),
    // matching the previous catalog mapper's behavior.
    val displayName = productName?.takeIf(String::isNotBlank)
        ?: name?.takeIf(String::isNotBlank)
        ?: return null

    return AiCatalogProduct(
        productId = resolvedId,
        name = displayName,
        productName = name ?: "",
        scientificName = scientificName?.takeIf(String::isNotBlank),
        priceEgp = price,
        strength = strength?.takeIf(String::isNotBlank),
        packSize = packSize?.takeIf(String::isNotBlank),
        form = form?.takeIf(String::isNotBlank),
        company = company?.takeIf(String::isNotBlank),
        route = route?.takeIf(String::isNotBlank),
        description = description?.takeIf(String::isNotBlank),
        imageUrl = imageUrl?.takeIf(String::isNotBlank),
    )
}

private val REMINDER_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val REMINDER_TIME_REGEX = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
private const val MAX_REMINDER_DURATION_DAYS = 90
