package com.medsy.data.reminders.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_reminders",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "sourceMessageId"], unique = true),
    ],
)
data class MedicationReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val sourceMessageId: Long?,
    val medicineName: String,
    val timesCsv: String,
    val durationDays: Int,
    val createdAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
    val isActive: Boolean,
)
