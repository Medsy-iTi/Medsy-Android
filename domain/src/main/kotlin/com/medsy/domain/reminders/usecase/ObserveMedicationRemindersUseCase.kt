package com.medsy.domain.reminders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.reminders.model.MedicationReminder
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMedicationRemindersUseCase @Inject constructor(
    private val repository: MedicationReminderRepository,
) {
    operator fun invoke(
        userId: Long,
    ): Flow<MedsyResult<List<MedicationReminder>, MedsyError.Local>> = repository.observe(userId)
}
