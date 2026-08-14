package com.medsy.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.reminders.usecase.DeleteMedicationReminderUseCase
import com.medsy.domain.reminders.usecase.ConsumeReminderBatteryPromptUseCase
import com.medsy.domain.reminders.usecase.ConsumeReminderNotificationPromptUseCase
import com.medsy.domain.reminders.usecase.CreateManualMedicationReminderUseCase
import com.medsy.domain.reminders.usecase.ObserveMedicationRemindersUseCase
import com.medsy.domain.reminders.usecase.RescheduleMedicationRemindersUseCase
import com.medsy.domain.reminders.usecase.ScheduleMedicationReminderUseCase
import com.medsy.domain.reminders.model.ReminderScheduleStatus
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MedicationRemindersViewModel @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val observeReminders: ObserveMedicationRemindersUseCase,
    private val deleteReminder: DeleteMedicationReminderUseCase,
    private val rescheduleReminders: RescheduleMedicationRemindersUseCase,
    private val createManualReminder: CreateManualMedicationReminderUseCase,
    private val scheduleReminder: ScheduleMedicationReminderUseCase,
    private val consumeBatteryPrompt: ConsumeReminderBatteryPromptUseCase,
    private val consumeNotificationPrompt: ConsumeReminderNotificationPromptUseCase,
) : ViewModel() {
    private var currentUserId = 0L
    private var remindersJob: Job? = null

    private val _state = MutableStateFlow(MedicationRemindersState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MedicationRemindersState(),
    )

    private val _effect = Channel<MedicationRemindersUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeSession().collect { session ->
                val userId = session?.user?.id ?: 0L
                if (userId != currentUserId) {
                    currentUserId = userId
                    _state.update {
                        it.copy(
                            manualReminderDraft = null,
                            isSavingManualReminder = false,
                        )
                    }
                    observeForUser(userId)
                }
            }
        }
    }

    fun onIntent(intent: MedicationRemindersUIIntent) {
        when (intent) {
            is MedicationRemindersUIIntent.ScreenResumed -> {
                _state.update {
                    it.copy(
                        notificationsEnabled = intent.notificationsEnabled,
                        exactAlarmsEnabled = intent.exactAlarmsEnabled,
                    )
                }
                if (intent.notificationsEnabled && currentUserId > 0L) {
                    viewModelScope.launch { rescheduleReminders(currentUserId) }
                }
            }

            MedicationRemindersUIIntent.BackClicked ->
                sendEffect(MedicationRemindersUIEffect.NavigateBack)

            MedicationRemindersUIIntent.RetryClicked -> observeForUser(currentUserId)
            MedicationRemindersUIIntent.EnableNotificationsClicked ->
                sendEffect(MedicationRemindersUIEffect.RequestNotificationPermission)

            is MedicationRemindersUIIntent.NotificationPermissionResult -> {
                _state.update { it.copy(notificationsEnabled = intent.granted) }
                if (intent.granted && currentUserId > 0L) {
                    viewModelScope.launch {
                        rescheduleReminders(currentUserId)
                            .onSuccess {
                                maybeShowBatteryPrompt()
                                sendEffect(
                                    MedicationRemindersUIEffect.ShowMessage(
                                        R.string.reminder_notifications_enabled,
                                        isSuccess = true,
                                    )
                                )
                            }
                            .onError { error ->
                                sendEffect(MedicationRemindersUIEffect.ShowMessage(error.toMessageRes()))
                            }
                    }
                } else if (!intent.granted) {
                    sendEffect(
                        MedicationRemindersUIEffect.ShowMessage(
                            R.string.reminder_notifications_disabled,
                        )
                    )
                }
            }

            MedicationRemindersUIIntent.EnableExactAlarmsClicked ->
                sendEffect(MedicationRemindersUIEffect.OpenExactAlarmSettings)

            MedicationRemindersUIIntent.AddReminderClicked ->
                _state.update { it.copy(manualReminderDraft = ManualReminderDraft()) }

            MedicationRemindersUIIntent.AddReminderDismissed -> {
                if (!_state.value.isSavingManualReminder) {
                    _state.update { it.copy(manualReminderDraft = null) }
                }
            }

            is MedicationRemindersUIIntent.ManualMedicineNameChanged ->
                updateManualDraft {
                    it.copy(
                        medicineName = intent.value.take(MAX_MEDICINE_NAME_LENGTH),
                        errorMessageRes = null,
                    )
                }

            is MedicationRemindersUIIntent.ManualTimeAdded ->
                updateManualDraft {
                    it.copy(
                        times = (it.times + intent.time).distinct().sorted(),
                        errorMessageRes = null,
                    )
                }

            is MedicationRemindersUIIntent.ManualTimeRemoved ->
                updateManualDraft {
                    it.copy(times = it.times - intent.time, errorMessageRes = null)
                }

            is MedicationRemindersUIIntent.ManualDurationChanged ->
                updateManualDraft {
                    it.copy(
                        durationDays = intent.durationDays.coerceIn(
                            MIN_DURATION_DAYS,
                            MAX_DURATION_DAYS,
                        ),
                        errorMessageRes = null,
                    )
                }

            MedicationRemindersUIIntent.SaveManualReminderClicked -> saveManualReminder()

            is MedicationRemindersUIIntent.DeleteClicked -> {
                val reminder = _state.value.reminders.firstOrNull { it.id == intent.reminderId }
                    ?: return
                _state.update { it.copy(pendingDelete = reminder) }
            }

            MedicationRemindersUIIntent.DeleteDismissed ->
                _state.update { it.copy(pendingDelete = null) }

            MedicationRemindersUIIntent.DeleteConfirmed -> deletePendingReminder()

            MedicationRemindersUIIntent.BatteryReliabilityDismissed ->
                _state.update { it.copy(isBatteryReliabilityDialogVisible = false) }

            MedicationRemindersUIIntent.BatterySettingsClicked -> {
                _state.update { it.copy(isBatteryReliabilityDialogVisible = false) }
                sendEffect(MedicationRemindersUIEffect.OpenBatteryOptimizationSettings)
            }
        }
    }

    private fun observeForUser(userId: Long) {
        remindersJob?.cancel()
        if (userId <= 0L) {
            _state.update { it.copy(reminders = emptyList(), isLoading = false) }
            return
        }
        _state.update { it.copy(isLoading = true, errorMessageRes = null) }
        remindersJob = viewModelScope.launch {
            observeReminders(userId).collect { result ->
                result.onSuccess { reminders ->
                    _state.update {
                        it.copy(reminders = reminders, isLoading = false, errorMessageRes = null)
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessageRes = error.toMessageRes())
                    }
                }
            }
        }
    }

    private fun deletePendingReminder() {
        val reminder = _state.value.pendingDelete ?: return
        _state.update { it.copy(pendingDelete = null) }
        viewModelScope.launch {
            deleteReminder(currentUserId, reminder.id)
                .onSuccess {
                    sendEffect(
                        MedicationRemindersUIEffect.ShowMessage(
                            R.string.reminder_deleted,
                            isSuccess = true,
                        )
                    )
                }
                .onError { error ->
                    sendEffect(MedicationRemindersUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun saveManualReminder() {
        val draft = _state.value.manualReminderDraft ?: return
        if (_state.value.isSavingManualReminder) return
        _state.update {
            it.copy(
                isSavingManualReminder = true,
                manualReminderDraft = draft.copy(errorMessageRes = null),
            )
        }
        viewModelScope.launch {
            createManualReminder(
                userId = currentUserId,
                medicineName = draft.medicineName,
                times = draft.times,
                durationDays = draft.durationDays,
            ).onSuccess { reminder ->
                _state.update {
                    it.copy(manualReminderDraft = null, isSavingManualReminder = false)
                }
                scheduleManualReminder(reminder.id)
            }.onError { error ->
                _state.update {
                    it.copy(
                        isSavingManualReminder = false,
                        manualReminderDraft = it.manualReminderDraft?.copy(
                            errorMessageRes = error.toMessageRes(),
                        ),
                    )
                }
            }
        }
    }

    private suspend fun scheduleManualReminder(reminderId: Long) {
        scheduleReminder(currentUserId, reminderId)
            .onSuccess { status ->
                when (status) {
                    ReminderScheduleStatus.EXACT,
                    ReminderScheduleStatus.INEXACT -> {
                        sendEffect(
                            MedicationRemindersUIEffect.ShowMessage(
                                R.string.reminder_manual_saved,
                                isSuccess = true,
                            )
                        )
                        showBatteryPromptAfterSchedule()
                    }

                    ReminderScheduleStatus.NOTIFICATIONS_DISABLED -> {
                        sendEffect(
                            MedicationRemindersUIEffect.ShowMessage(
                                R.string.reminder_saved_notifications_disabled,
                                isSuccess = true,
                            )
                        )
                        if (consumeNotificationPrompt()) {
                            sendEffect(MedicationRemindersUIEffect.RequestNotificationPermission)
                        }
                    }

                    ReminderScheduleStatus.EXPIRED_OR_MISSING ->
                        sendEffect(
                            MedicationRemindersUIEffect.ShowMessage(
                                R.string.reminder_saved_schedule_failed,
                            )
                        )
                }
            }
            .onError {
                sendEffect(
                    MedicationRemindersUIEffect.ShowMessage(
                        R.string.reminder_saved_schedule_failed,
                    )
                )
            }
    }

    private suspend fun showBatteryPromptAfterSchedule() {
        if (consumeBatteryPrompt()) {
            _state.update { it.copy(isBatteryReliabilityDialogVisible = true) }
        }
    }

    private fun updateManualDraft(transform: (ManualReminderDraft) -> ManualReminderDraft) {
        _state.update { state ->
            state.copy(manualReminderDraft = state.manualReminderDraft?.let(transform))
        }
    }

    private suspend fun maybeShowBatteryPrompt() {
        if (_state.value.reminders.any { it.isActive } && consumeBatteryPrompt()) {
            _state.update { it.copy(isBatteryReliabilityDialogVisible = true) }
        }
    }

    private fun sendEffect(effect: MedicationRemindersUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private companion object {
        const val MIN_DURATION_DAYS = 1
        const val MAX_DURATION_DAYS = 90
        const val MAX_MEDICINE_NAME_LENGTH = 100
    }
}
