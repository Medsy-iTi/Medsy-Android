package com.medsy.presentation.reminders

import java.time.LocalTime

sealed interface MedicationRemindersUIIntent {
    data class ScreenResumed(
        val notificationsEnabled: Boolean,
        val exactAlarmsEnabled: Boolean,
    ) : MedicationRemindersUIIntent
    data object BackClicked : MedicationRemindersUIIntent
    data object RetryClicked : MedicationRemindersUIIntent
    data object EnableNotificationsClicked : MedicationRemindersUIIntent
    data class NotificationPermissionResult(val granted: Boolean) : MedicationRemindersUIIntent
    data object EnableExactAlarmsClicked : MedicationRemindersUIIntent
    data object AddReminderClicked : MedicationRemindersUIIntent
    data object AddReminderDismissed : MedicationRemindersUIIntent
    data class ManualMedicineNameChanged(val value: String) : MedicationRemindersUIIntent
    data class ManualTimeAdded(val time: LocalTime) : MedicationRemindersUIIntent
    data class ManualTimeRemoved(val time: LocalTime) : MedicationRemindersUIIntent
    data class ManualDurationChanged(val durationDays: Int) : MedicationRemindersUIIntent
    data object SaveManualReminderClicked : MedicationRemindersUIIntent
    data class DeleteClicked(val reminderId: Long) : MedicationRemindersUIIntent
    data object DeleteDismissed : MedicationRemindersUIIntent
    data object DeleteConfirmed : MedicationRemindersUIIntent
    data object BatteryReliabilityDismissed : MedicationRemindersUIIntent
    data object BatterySettingsClicked : MedicationRemindersUIIntent
}
