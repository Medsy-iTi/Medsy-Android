package com.medsy.domain.reminders.usecase

import com.medsy.domain.reminders.repository.MedicationReminderRepository
import javax.inject.Inject

class ConsumeReminderNotificationPromptUseCase @Inject constructor(
    private val repository: MedicationReminderRepository,
) {
    suspend operator fun invoke(): Boolean = repository.consumeNotificationPermissionPrompt()
}
