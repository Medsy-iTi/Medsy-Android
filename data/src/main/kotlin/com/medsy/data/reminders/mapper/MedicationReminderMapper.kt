package com.medsy.data.reminders.mapper

import com.medsy.data.reminders.local.MedicationReminderEntity
import com.medsy.domain.reminders.model.MedicationReminder
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun MedicationReminderEntity.toDomain(): MedicationReminder = MedicationReminder(
    id = id,
    medicineName = medicineName,
    times = timesCsv.split(',').mapNotNull { value ->
        runCatching { LocalTime.parse(value, TIME_FORMAT) }.getOrNull()
    },
    durationDays = durationDays,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    expiresAt = Instant.ofEpochMilli(expiresAtEpochMillis),
    isActive = isActive,
)

internal fun List<LocalTime>.toCsv(): String =
    sorted().joinToString(",") { it.format(TIME_FORMAT) }

internal val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
