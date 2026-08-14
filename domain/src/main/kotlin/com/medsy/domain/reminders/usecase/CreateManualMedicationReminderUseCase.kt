package com.medsy.domain.reminders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.reminders.model.CreateMedicationReminderParams
import com.medsy.domain.reminders.model.MedicationReminder
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import java.time.LocalTime
import javax.inject.Inject

class CreateManualMedicationReminderUseCase @Inject constructor(
    private val repository: MedicationReminderRepository,
) {
    suspend operator fun invoke(
        userId: Long,
        medicineName: String,
        times: List<LocalTime>,
        durationDays: Int,
    ): MedsyResult<MedicationReminder, MedsyError> {
        val normalizedName = medicineName.trim()
        val normalizedTimes = times.distinct().sorted()
        if (
            userId <= 0L ||
            normalizedName.isEmpty() ||
            normalizedTimes.isEmpty() ||
            durationDays !in MIN_DURATION_DAYS..MAX_DURATION_DAYS
        ) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_REMINDER_DETAILS)
        }

        return repository.create(
            userId = userId,
            params = CreateMedicationReminderParams(
                sourceMessageId = null,
                medicineName = normalizedName,
                times = normalizedTimes,
                durationDays = durationDays,
            ),
        )
    }

    private companion object {
        const val MIN_DURATION_DAYS = 1
        const val MAX_DURATION_DAYS = 90
    }
}
