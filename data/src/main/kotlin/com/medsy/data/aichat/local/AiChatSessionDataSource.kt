package com.medsy.data.aichat.local

import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatSender
import com.medsy.domain.aichat.model.AiChatSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class AiChatSessionDataSource @Inject constructor() {
    private val _state = MutableStateFlow(AiChatSession())
    val state: StateFlow<AiChatSession> = _state.asStateFlow()

    private var nextMessageId = 1L
    private var sessionGeneration = 0L

    @Synchronized
    fun beginQuestion(question: String): Long {
        val generation = sessionGeneration
        _state.update { current ->
            val lastQuestion = current.messages
                .lastOrNull()
                ?.content as? AiChatContent.UserText
            val messages = if (lastQuestion?.value == question) {
                current.messages
            } else {
                current.messages + AiChatMessage(
                    id = nextMessageId++,
                    sender = AiChatSender.USER,
                    content = AiChatContent.UserText(question),
                )
            }
            current.copy(messages = messages, isResponding = true)
        }
        return generation
    }

    @Synchronized
    fun completeAnswer(
        generation: Long,
        response: AiChatContent.AssistantResponse,
    ) {
        if (generation != sessionGeneration) return
        _state.update { current ->
            current.copy(
                messages = current.messages + AiChatMessage(
                    id = nextMessageId++,
                    sender = AiChatSender.ASSISTANT,
                    content = response,
                ),
                isResponding = false,
            )
        }
    }

    @Synchronized
    fun failQuestion(generation: Long) {
        if (generation != sessionGeneration) return
        _state.update { it.copy(isResponding = false) }
    }

    @Synchronized
    fun reset() {
        sessionGeneration += 1L
        nextMessageId = 1L
        _state.value = AiChatSession()
    }
}
