package com.medsy.presentation.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatOrderDate(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) return ""
    return runCatching {
        val zonedDateTime = runCatching { ZonedDateTime.parse(rawDate) }.getOrNull()
        val localDateTime = zonedDateTime?.toLocalDateTime()
            ?: runCatching { LocalDateTime.parse(rawDate) }.getOrNull()
            ?: runCatching {
                Instant.parse(rawDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }.getOrNull()

        localDateTime?.let {
            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
            it.format(formatter)
        }
    }.getOrNull() ?: rawDate
}
