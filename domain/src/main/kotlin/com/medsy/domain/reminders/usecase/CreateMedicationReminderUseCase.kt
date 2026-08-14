package com.medsy.domain.reminders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.reminders.model.CreateMedicationReminderParams
import com.medsy.domain.reminders.model.MedicationReminder
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import javax.inject.Inject

class CreateMedicationReminderUseCase @Inject constructor(
    private val repository: MedicationReminderRepository,
) {
    suspend operator fun invoke(
        userId: Long,
        params: CreateMedicationReminderParams,
    ): MedsyResult<MedicationReminder, MedsyError.Local> = repository.create(userId, params)
}
