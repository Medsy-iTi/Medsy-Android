package com.medsy.domain.reminders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.reminders.model.ReminderScheduleStatus
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import javax.inject.Inject

class ScheduleMedicationReminderUseCase @Inject constructor(
    private val repository: MedicationReminderRepository,
) {
    suspend operator fun invoke(
        userId: Long,
        reminderId: Long,
    ): MedsyResult<ReminderScheduleStatus, MedsyError.Local> =
        repository.schedule(userId, reminderId)
}
