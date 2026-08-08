package com.medsy.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.usecase.LoadAiChatHistoryUseCase
import com.medsy.domain.aichat.usecase.ObserveAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.SendAiChatMessageUseCase
import com.medsy.domain.aichat.usecase.StartNewAiChatUseCase
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.prescription.model.PrescriptionImage
import com.medsy.domain.prescription.usecase.DeletePrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.ImportPrescriptionImageUseCase
import com.medsy.domain.prescription.usecase.PreparePrescriptionCaptureUseCase
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
) : ViewModel() {
    private var requestGeneration = 0L
    private var pendingInitialPrompt: String? = null
    private var initialPromptConsumed = false
    private var pendingCameraImage: PrescriptionImage? = null

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
                .onSuccess {
                    // Actions (added-to-cart / create-request) render as cards on
                    // the assistant message itself; nothing to execute here. The
                    // backend already performed any cart mutation.
                    if (generation == requestGeneration) {
                        _state.update {
                            it.copy(failedSubmission = null, errorMessageRes = null)
                        }
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
