package com.medsy.domain.reminders.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.reminders.model.CreateMedicationReminderParams
import com.medsy.domain.reminders.model.MedicationReminder
import com.medsy.domain.reminders.model.ReminderScheduleStatus
import kotlinx.coroutines.flow.Flow

interface MedicationReminderRepository {
    fun observe(userId: Long): Flow<MedsyResult<List<MedicationReminder>, MedsyError.Local>>

    suspend fun create(
        userId: Long,
        params: CreateMedicationReminderParams,
    ): MedsyResult<MedicationReminder, MedsyError.Local>

    suspend fun schedule(
        userId: Long,
        reminderId: Long,
    ): MedsyResult<ReminderScheduleStatus, MedsyError.Local>

    suspend fun delete(userId: Long, reminderId: Long): EmptyMedsyResult<MedsyError.Local>

    suspend fun rescheduleActive(userId: Long): EmptyMedsyResult<MedsyError.Local>

    suspend fun cancelScheduled(userId: Long): EmptyMedsyResult<MedsyError.Local>

    suspend fun consumeNotificationPermissionPrompt(): Boolean

    suspend fun consumeBatteryReliabilityPrompt(): Boolean
}
