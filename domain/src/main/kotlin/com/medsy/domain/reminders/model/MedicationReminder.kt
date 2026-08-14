package com.medsy.domain.reminders.model

import java.time.Instant
import java.time.LocalTime

data class MedicationReminder(
    val id: Long,
    val medicineName: String,
    val times: List<LocalTime>,
    val durationDays: Int,
    val createdAt: Instant,
    val expiresAt: Instant,
    val isActive: Boolean,
)

data class CreateMedicationReminderParams(
    val sourceMessageId: Long?,
    val medicineName: String,
    val times: List<LocalTime>,
    val durationDays: Int,
)

enum class ReminderScheduleStatus {
    EXACT,
    INEXACT,
    NOTIFICATIONS_DISABLED,
    EXPIRED_OR_MISSING,
}
