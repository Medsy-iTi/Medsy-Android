package com.medsy.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.model.AiChatLanguage
import com.medsy.domain.aichat.usecase.ObserveAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.ResetAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.SubmitAiChatActionUseCase
import com.medsy.domain.cart.usecase.AddCartItemUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
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
    private val addCartItem: AddCartItemUseCase,
) : ViewModel() {
    private var requestGeneration = 0L
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
                    )
                }
            }
        }
    }

    fun onIntent(intent: AiChatUIIntent) {
        when (intent) {
            is AiChatUIIntent.InputChanged -> _state.update {
                it.copy(input = intent.value.take(MAX_QUESTION_LENGTH))
            }
            AiChatUIIntent.SendClicked -> sendInput()
            is AiChatUIIntent.QuickActionClicked -> askCatalog(intent.question)
            AiChatUIIntent.RetryCatalogQuestion ->
                _state.value.failedQuestion?.let(::askCatalog)
            AiChatUIIntent.VoiceClicked -> sendEffect(AiChatUIEffect.LaunchVoiceInput)
            is AiChatUIIntent.VoiceResult -> intent.text
                ?.takeIf(String::isNotBlank)
                ?.let { value ->
                    _state.update {
                        it.copy(input = value.take(MAX_QUESTION_LENGTH))
                    }
                }
            AiChatUIIntent.NewChatClicked ->
                _state.update { it.copy(isNewChatDialogVisible = true) }
            AiChatUIIntent.DismissNewChat ->
                _state.update { it.copy(isNewChatDialogVisible = false) }
            AiChatUIIntent.ConfirmNewChat -> {
                requestGeneration += 1L
                resetSession()
                _state.value = AiChatState()
            }
            is AiChatUIIntent.ProductClicked ->
                sendEffect(AiChatUIEffect.OpenProduct(intent.productId))
            is AiChatUIIntent.AddToCartClicked -> addToCart(intent.productId)
        }
    }

    private fun sendInput() {
        val input = _state.value.input.trim()
        if (input.isEmpty() || _state.value.isResponding) return
        askCatalog(input)
    }

    private fun askCatalog(question: String) {
        val normalizedQuestion = question.trim().take(MAX_QUESTION_LENGTH)
        if (normalizedQuestion.isEmpty() || _state.value.isResponding) return
        val generation = ++requestGeneration
        _state.update {
            it.copy(
                input = "",
                failedQuestion = null,
                errorMessageRes = null,
            )
        }
        viewModelScope.launch {
            submitAction(
                AiChatAction(
                    question = normalizedQuestion,
                    language = if (Locale.getDefault().language == ARABIC_LANGUAGE_CODE) {
                        AiChatLanguage.ARABIC
                    } else {
                        AiChatLanguage.ENGLISH
                    },
                    limit = CATALOG_RESULT_LIMIT,
                )
            ).onSuccess {
                if (generation == requestGeneration) {
                    _state.update {
                        it.copy(failedQuestion = null, errorMessageRes = null)
                    }
                }
            }.onError { error ->
                if (generation == requestGeneration) {
                    _state.update {
                        it.copy(
                            failedQuestion = normalizedQuestion,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
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

    private fun sendEffect(effect: AiChatUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private companion object {
        const val ARABIC_LANGUAGE_CODE = "ar"
        const val CATALOG_RESULT_LIMIT = 5
        const val MAX_QUESTION_LENGTH = 500
    }
}
