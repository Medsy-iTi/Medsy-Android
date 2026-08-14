package com.medsy.domain.reminders.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import javax.inject.Inject

class RescheduleMedicationRemindersUseCase @Inject constructor(
    private val repository: MedicationReminderRepository,
) {
    suspend operator fun invoke(userId: Long): EmptyMedsyResult<MedsyError.Local> =
        repository.rescheduleActive(userId)
}
