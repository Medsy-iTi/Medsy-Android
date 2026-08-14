package com.medsy.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.model.AiReminderInfo
import com.medsy.domain.aichat.usecase.LoadAiChatHistoryUseCase
import com.medsy.domain.aichat.usecase.ObserveAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.SendAiChatMessageUseCase
import com.medsy.domain.aichat.usecase.StartNewAiChatUseCase
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.usecase.DeletePrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.ImportPrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.PreparePrescriptionCaptureUseCase
import com.medsy.domain.reminders.model.CreateMedicationReminderParams
import com.medsy.domain.reminders.model.ReminderScheduleStatus
import com.medsy.domain.reminders.usecase.ConsumeReminderBatteryPromptUseCase
import com.medsy.domain.reminders.usecase.ConsumeReminderNotificationPromptUseCase
import com.medsy.domain.reminders.usecase.CreateMedicationReminderUseCase
import com.medsy.domain.reminders.usecase.ScheduleMedicationReminderUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AiChatViewModel @Inject constructor(
    observeSession: ObserveAiChatSessionUseCase,
    private val loadHistory: LoadAiChatHistoryUseCase,
    private val sendMessage: SendAiChatMessageUseCase,
    private val startNewChat: StartNewAiChatUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val prepareCapture: PreparePrescriptionCaptureUseCase,
    private val importImage: ImportPrescriptionImageUseCase,
    private val deleteImage: DeletePrescriptionImageUseCase,
    private val observeAuthSession: ObserveSessionUseCase,
    private val createReminder: CreateMedicationReminderUseCase,
    private val scheduleReminder: ScheduleMedicationReminderUseCase,
    private val consumeBatteryPrompt: ConsumeReminderBatteryPromptUseCase,
    private val consumeNotificationPrompt: ConsumeReminderNotificationPromptUseCase,
) : ViewModel() {
    private var requestGeneration = 0L
    private var pendingInitialPrompt: String? = null
    private var initialPromptConsumed = false
    private var pendingCameraImage: PrescriptionImage? = null
    private var currentUserId = 0L

    private val _state = MutableStateFlow(AiChatState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = AiChatState(),
    )

    private val _effect = Channel<AiChatUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeAuthSession().collect { session ->
                currentUserId = session?.user?.id ?: 0L
            }
        }
        viewModelScope.launch {
            observeSession().collect { session ->
                _state.update {
                    it.copy(
                        messages = session.messages,
                        isResponding = session.isResponding,
                        isLoadingHistory = !session.isHydrated && it.historyErrorRes == null,
                    )
                }
                if (session.isHydrated) {
                    consumeInitialPrompt()
                }
            }
        }
        fetchHistory()
    }

    /** Buffered until the history hydrates, then auto-sent exactly once. */
    fun setInitialPrompt(prompt: String?) {
        if (initialPromptConsumed || prompt.isNullOrBlank()) return
        pendingInitialPrompt = prompt
    }

    fun onIntent(intent: AiChatUIIntent) {
        when (intent) {
            is AiChatUIIntent.InputChanged -> _state.update {
                it.copy(input = intent.value.take(MAX_MESSAGE_LENGTH))
            }

            AiChatUIIntent.SendClicked -> sendCurrentInput()
            is AiChatUIIntent.QuickActionClicked ->
                submit(AiChatOutgoingMessage.Text(intent.question))

            AiChatUIIntent.RetrySend ->
                _state.value.failedSubmission?.let(::submit)

            AiChatUIIntent.RetryHistory -> fetchHistory()
            AiChatUIIntent.VoiceClicked -> sendEffect(AiChatUIEffect.LaunchVoiceInput)
            is AiChatUIIntent.VoiceResult -> intent.text
                ?.takeIf(String::isNotBlank)
                ?.let { value ->
                    _state.update { it.copy(input = value.take(MAX_MESSAGE_LENGTH)) }
                }

            AiChatUIIntent.NewChatClicked ->
                _state.update { it.copy(isNewChatDialogVisible = true) }

            AiChatUIIntent.DismissNewChat ->
                _state.update { it.copy(isNewChatDialogVisible = false) }

            AiChatUIIntent.ConfirmNewChat -> confirmNewChat()
            is AiChatUIIntent.ProductClicked ->
                sendEffect(AiChatUIEffect.OpenProduct(intent.productId))

            is AiChatUIIntent.AddToCartClicked -> addToCart(intent.productId)

            AiChatUIIntent.AttachClicked ->
                _state.update { it.copy(isAttachSheetVisible = true) }

            AiChatUIIntent.AttachSheetDismissed ->
                _state.update { it.copy(isAttachSheetVisible = false) }

            AiChatUIIntent.CameraClicked -> launchCamera()
            AiChatUIIntent.GalleryClicked -> {
                _state.update { it.copy(isAttachSheetVisible = false) }
                sendEffect(AiChatUIEffect.LaunchGallery)
            }

            is AiChatUIIntent.CameraCaptureCompleted -> onCameraResult(intent.success)
            is AiChatUIIntent.GalleryImageSelected -> onGalleryResult(intent.uri)
            AiChatUIIntent.RemoveAttachmentClicked -> removeAttachment()

            is AiChatUIIntent.EmergencyCallClicked ->
                sendEffect(AiChatUIEffect.DialNumber(intent.number))

            is AiChatUIIntent.CategoryClicked ->
                sendEffect(AiChatUIEffect.OpenCategory(intent.categoryId, intent.categoryName))

            AiChatUIIntent.ViewCartClicked -> sendEffect(AiChatUIEffect.NavigateToCartTab)
            AiChatUIIntent.ConfirmRequestClicked ->
                sendEffect(AiChatUIEffect.NavigateToCartRequest)

            is AiChatUIIntent.NotificationPermissionResult -> {
                if (intent.granted) {
                    scheduleSavedReminder(intent.reminderId, intent.messageId)
                } else {
                    updateReminderStatus(intent.messageId, ReminderUiStatus.NOTIFICATIONS_DISABLED)
                    sendEffect(AiChatUIEffect.ShowMessage(R.string.reminder_notifications_disabled))
                }
            }

            AiChatUIIntent.EnableExactAlarmsClicked ->
                sendEffect(AiChatUIEffect.OpenExactAlarmSettings)

            AiChatUIIntent.UseApproximateAlarmsClicked -> {
                _state.update {
                    it.copy(
                        pendingExactAlarmReminderId = null,
                        pendingExactAlarmMessageId = null,
                    )
                }
                maybeShowBatteryPrompt()
            }

            AiChatUIIntent.ExactAlarmSettingsReturned -> {
                val reminderId = _state.value.pendingExactAlarmReminderId ?: return
                val messageId = _state.value.pendingExactAlarmMessageId ?: return
                _state.update {
                    it.copy(
                        pendingExactAlarmReminderId = null,
                        pendingExactAlarmMessageId = null,
                    )
                }
                scheduleSavedReminder(reminderId, messageId, offerExactAccess = false)
            }

            AiChatUIIntent.BatteryReliabilityDismissed ->
                _state.update { it.copy(isBatteryReliabilityDialogVisible = false) }

            AiChatUIIntent.BatterySettingsClicked -> {
                _state.update { it.copy(isBatteryReliabilityDialogVisible = false) }
                sendEffect(AiChatUIEffect.OpenBatteryOptimizationSettings)
            }
        }
    }

    private fun fetchHistory() {
        _state.update { it.copy(isLoadingHistory = true, historyErrorRes = null) }
        viewModelScope.launch {
            loadHistory()
                .onSuccess {
                    _state.update { it.copy(isLoadingHistory = false) }
                    consumeInitialPrompt()
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoadingHistory = false,
                            historyErrorRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }

    private fun consumeInitialPrompt() {
        val prompt = pendingInitialPrompt ?: return
        if (initialPromptConsumed) return
        initialPromptConsumed = true
        pendingInitialPrompt = null
        if (_state.value.isResponding) {
            _state.update { it.copy(input = prompt.take(MAX_MESSAGE_LENGTH)) }
        } else {
            submit(AiChatOutgoingMessage.Text(prompt))
        }
    }

    private fun sendCurrentInput() {
        val current = _state.value
        val text = current.input.trim()
        val attachment = current.pendingAttachment
        val message = when {
            attachment != null -> AiChatOutgoingMessage.Image(attachment.image, text)
            text.isNotEmpty() -> AiChatOutgoingMessage.Text(text)
            else -> return
        }
        submit(message)
    }

    private fun submit(message: AiChatOutgoingMessage) {
        if (!_state.value.canSend) return
        val normalized = when (message) {
            is AiChatOutgoingMessage.Text -> {
                val text = message.message.trim().take(MAX_MESSAGE_LENGTH)
                if (text.isEmpty()) return
                AiChatOutgoingMessage.Text(text)
            }

            is AiChatOutgoingMessage.Image -> AiChatOutgoingMessage.Image(
                image = message.image,
                caption = message.caption.trim().take(MAX_MESSAGE_LENGTH),
            )
        }
        val generation = ++requestGeneration
        _state.update {
            it.copy(
                input = "",
                pendingAttachment = null,
                failedSubmission = null,
                errorMessageRes = null,
            )
        }
        viewModelScope.launch {
            sendMessage(normalized)
                .onSuccess { result ->
                    // Actions (added-to-cart / create-request) render as cards on
                    // the assistant message itself; nothing to execute here. The
                    // backend already performed any cart mutation.
                    if (generation == requestGeneration) {
                        _state.update {
                            it.copy(failedSubmission = null, errorMessageRes = null)
                        }
                        result.reminder?.let { reminder -> saveReminder(reminder) }
                    }
                }
                .onError { error ->
                    if (generation == requestGeneration) {
                        _state.update {
                            it.copy(
                                failedSubmission = normalized,
                                errorMessageRes = error.toMessageRes(),
                            )
                        }
                    }
                }
        }
    }

    private suspend fun saveReminder(reminder: AiReminderInfo) {
        val userId = currentUserId
        updateReminderStatus(reminder.sourceMessageId, ReminderUiStatus.SAVING)
        if (userId <= 0L) {
            updateReminderStatus(reminder.sourceMessageId, ReminderUiStatus.SAVE_FAILED)
            return
        }
        createReminder(
            userId = userId,
            params = CreateMedicationReminderParams(
                sourceMessageId = reminder.sourceMessageId,
                medicineName = reminder.medicineName,
                times = reminder.times,
                durationDays = reminder.durationDays,
            ),
        ).onSuccess { saved ->
            if (consumeNotificationPrompt()) {
                sendEffect(
                    AiChatUIEffect.RequestNotificationPermission(
                        reminderId = saved.id,
                        messageId = reminder.sourceMessageId,
                    )
                )
            } else {
                scheduleSavedReminder(saved.id, reminder.sourceMessageId)
            }
        }.onError { error ->
            updateReminderStatus(reminder.sourceMessageId, ReminderUiStatus.SAVE_FAILED)
            sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
        }
    }

    private fun scheduleSavedReminder(
        reminderId: Long,
        messageId: Long,
        offerExactAccess: Boolean = true,
    ) {
        val userId = currentUserId
        if (userId <= 0L) return
        viewModelScope.launch {
            scheduleReminder(userId, reminderId)
                .onSuccess { status ->
                    when (status) {
                        ReminderScheduleStatus.EXACT -> {
                            updateReminderStatus(messageId, ReminderUiStatus.SCHEDULED)
                            maybeShowBatteryPrompt()
                        }

                        ReminderScheduleStatus.INEXACT -> {
                            updateReminderStatus(messageId, ReminderUiStatus.SCHEDULED)
                            if (offerExactAccess) {
                                _state.update {
                                    it.copy(
                                        pendingExactAlarmReminderId = reminderId,
                                        pendingExactAlarmMessageId = messageId,
                                    )
                                }
                            } else {
                                maybeShowBatteryPrompt()
                            }
                        }

                        ReminderScheduleStatus.NOTIFICATIONS_DISABLED ->
                            updateReminderStatus(
                                messageId,
                                ReminderUiStatus.NOTIFICATIONS_DISABLED,
                            )

                        ReminderScheduleStatus.EXPIRED_OR_MISSING ->
                            updateReminderStatus(messageId, ReminderUiStatus.SAVE_FAILED)
                    }
                }
                .onError { error ->
                    updateReminderStatus(messageId, ReminderUiStatus.SAVE_FAILED)
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun maybeShowBatteryPrompt() {
        viewModelScope.launch {
            if (consumeBatteryPrompt()) {
                _state.update { it.copy(isBatteryReliabilityDialogVisible = true) }
            }
        }
    }

    private fun updateReminderStatus(messageId: Long, status: ReminderUiStatus) {
        _state.update {
            it.copy(reminderStatuses = it.reminderStatuses + (messageId to status))
        }
    }

    private fun confirmNewChat() {
        _state.update { it.copy(isNewChatDialogVisible = false) }
        viewModelScope.launch {
            startNewChat()
                .onSuccess {
                    requestGeneration += 1L
                    _state.update {
                        it.copy(
                            input = "",
                            pendingAttachment = null,
                            failedSubmission = null,
                            errorMessageRes = null,
                            reminderStatuses = emptyMap(),
                            pendingExactAlarmReminderId = null,
                            pendingExactAlarmMessageId = null,
                        )
                    }
                }
                .onError { error ->
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    /** Explicit product-card button only — chat ADD_TO_CART actions are server-side. */
    private fun addToCart(productId: Int) {
        viewModelScope.launch {
            addCartItem(productId)
                .onSuccess {
                    sendEffect(AiChatUIEffect.ShowMessage(R.string.ai_chat_added_to_cart, isSuccess = true))
                }
                .onError { error ->
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun launchCamera() {
        _state.update { it.copy(isAttachSheetVisible = false) }
        viewModelScope.launch {
            prepareCapture()
                .onSuccess { image ->
                    pendingCameraImage = image
                    sendEffect(AiChatUIEffect.LaunchCamera(image.uri))
                }
                .onError { error ->
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun onCameraResult(success: Boolean) {
        val image = pendingCameraImage ?: return
        pendingCameraImage = null
        if (success) {
            setAttachment(image)
        } else {
            viewModelScope.launch { deleteImage(image) }
        }
    }

    private fun onGalleryResult(uri: String?) {
        if (uri == null) return
        viewModelScope.launch {
            importImage(uri)
                .onSuccess(::setAttachment)
                .onError { error ->
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun setAttachment(image: PrescriptionImage) {
        val previous = _state.value.pendingAttachment
        _state.update { it.copy(pendingAttachment = PendingAttachment(image)) }
        previous?.let { viewModelScope.launch { deleteImage(it.image) } }
    }

    private fun removeAttachment() {
        val attachment = _state.value.pendingAttachment ?: return
        _state.update { it.copy(pendingAttachment = null) }
        viewModelScope.launch { deleteImage(attachment.image) }
    }

    private fun sendEffect(effect: AiChatUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private companion object {
        const val MAX_MESSAGE_LENGTH = 500
    }
}
