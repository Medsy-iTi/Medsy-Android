package com.medsy.presentation.reminders

import androidx.annotation.StringRes
import com.medsy.domain.reminders.model.MedicationReminder
import java.time.LocalTime

data class MedicationRemindersState(
    val reminders: List<MedicationReminder> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val notificationsEnabled: Boolean = true,
    val exactAlarmsEnabled: Boolean = true,
    val pendingDelete: MedicationReminder? = null,
    val isBatteryReliabilityDialogVisible: Boolean = false,
    val manualReminderDraft: ManualReminderDraft? = null,
    val isSavingManualReminder: Boolean = false,
) {
    val isEmpty: Boolean get() = !isLoading && errorMessageRes == null && reminders.isEmpty()
}

data class ManualReminderDraft(
    val medicineName: String = "",
    val times: List<LocalTime> = emptyList(),
    val durationDays: Int = 1,
    @StringRes val errorMessageRes: Int? = null,
)
