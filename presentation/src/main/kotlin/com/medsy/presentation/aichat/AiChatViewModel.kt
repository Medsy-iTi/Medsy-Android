package com.medsy.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.usecase.ObserveAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.ResetAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.SubmitAiChatActionUseCase
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.cart.usecase.AddCartItemsUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
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
    private val submitAction: SubmitAiChatActionUseCase,
    private val resetSession: ResetAiChatSessionUseCase,
    private val preparePrescriptionCapture: PreparePrescriptionCaptureUseCase,
    private val addCartItem: AddCartItemUseCase,
    private val addCartItems: AddCartItemsUseCase,
) : ViewModel() {
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
                        isAiCallInProgress = session.isAiCallInProgress,
                        isReminderConfirmed = session.isReminderConfirmed,
                    )
                }
            }
        }
    }

    fun onIntent(intent: AiChatUIIntent) {
        when (intent) {
            is AiChatUIIntent.InputChanged -> _state.update { it.copy(input = intent.value) }
            AiChatUIIntent.SendClicked -> sendInput()
            is AiChatUIIntent.QuickActionClicked -> submit(
                AiChatAction.SelectScenario(intent.scenario)
            )
            AiChatUIIntent.AttachmentClicked ->
                _state.update { it.copy(isAttachmentMenuVisible = true) }
            AiChatUIIntent.DismissAttachmentMenu ->
                _state.update { it.copy(isAttachmentMenuVisible = false) }
            is AiChatUIIntent.CameraClicked -> prepareCamera(intent.kind)
            is AiChatUIIntent.GalleryClicked -> {
                _state.update {
                    it.copy(
                        isAttachmentMenuVisible = false,
                        pendingImageKind = intent.kind,
                    )
                }
                sendEffect(AiChatUIEffect.LaunchGallery(intent.kind))
            }
            is AiChatUIIntent.ImageSelected -> {
                _state.update { it.copy(pendingImageKind = null) }
                intent.uri?.let {
                    submit(AiChatAction.ImageSelected(intent.kind, it))
                }
            }
            AiChatUIIntent.VoiceClicked -> sendEffect(AiChatUIEffect.LaunchVoiceInput)
            is AiChatUIIntent.VoiceResult -> intent.text
                ?.takeIf(String::isNotBlank)
                ?.let { value -> _state.update { it.copy(input = value) } }
            AiChatUIIntent.NewChatClicked ->
                _state.update { it.copy(isNewChatDialogVisible = true) }
            AiChatUIIntent.DismissNewChat ->
                _state.update { it.copy(isNewChatDialogVisible = false) }
            AiChatUIIntent.ConfirmNewChat -> {
                resetSession()
                _state.value = AiChatState()
            }
            is AiChatUIIntent.AddToCartClicked -> addToCart(intent.productId)
            is AiChatUIIntent.ReorderClicked -> reorder(intent.productIds)
            is AiChatUIIntent.CallPharmacyClicked ->
                sendEffect(AiChatUIEffect.DialPhone(intent.phoneNumber))
            is AiChatUIIntent.DirectionsClicked -> sendEffect(
                AiChatUIEffect.OpenDirections(
                    latitude = intent.pharmacy.latitude,
                    longitude = intent.pharmacy.longitude,
                    label = intent.pharmacy.name,
                )
            )
            AiChatUIIntent.StartAiCallClicked ->
                submit(AiChatAction.StartAiCall)
            AiChatUIIntent.EndAiCallClicked ->
                submit(AiChatAction.EndAiCall)
            AiChatUIIntent.CallAmbulanceClicked ->
                sendEffect(AiChatUIEffect.DialPhone(AMBULANCE_NUMBER))
            AiChatUIIntent.FindNearestHospitalClicked ->
                sendEffect(AiChatUIEffect.OpenMapSearch(R.string.ai_chat_nearest_hospital_query))
            AiChatUIIntent.SetReminderClicked -> {
                submit(AiChatAction.ConfirmReminder)
                sendEffect(AiChatUIEffect.ShowMessage(R.string.ai_chat_reminder_confirmed))
            }
        }
    }

    private fun sendInput() {
        val input = _state.value.input.trim()
        if (input.isEmpty() || _state.value.isResponding) return
        _state.update { it.copy(input = "") }
        submit(AiChatAction.SendText(input))
    }

    private fun submit(action: AiChatAction) {
        if (_state.value.isResponding) return
        viewModelScope.launch {
            submitAction(action).onError {
                sendEffect(AiChatUIEffect.ShowMessage(R.string.ai_chat_action_error))
            }
        }
    }

    private fun prepareCamera(kind: com.medsy.domain.aichat.model.AiChatImageKind) {
        _state.update { it.copy(isAttachmentMenuVisible = false, pendingImageKind = kind) }
        viewModelScope.launch {
            preparePrescriptionCapture()
                .onSuccess { image ->
                    sendEffect(AiChatUIEffect.LaunchCamera(kind, image.uri))
                }
                .onError { error ->
                    _state.update { it.copy(pendingImageKind = null) }
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun addToCart(productId: Int) {
        viewModelScope.launch {
            addCartItem(productId)
                .onSuccess {
                    sendEffect(AiChatUIEffect.ShowMessage(R.string.ai_chat_added_to_cart))
                }
                .onError { error ->
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun reorder(productIds: List<Int>) {
        viewModelScope.launch {
            addCartItems(productIds.map { CartItemInput(productId = it, quantity = 1) })
                .onSuccess {
                    sendEffect(AiChatUIEffect.ShowMessage(R.string.ai_chat_reorder_added))
                }
                .onError { error ->
                    sendEffect(AiChatUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun sendEffect(effect: AiChatUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private companion object {
        const val AMBULANCE_NUMBER = "123"
    }
}
