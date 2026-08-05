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

/**
 * In-memory chat session. Server messages keep their positive server ids;
 * optimistic local user bubbles use negative ids so they can never collide
 * with anything the history endpoint returns later.
 */
@Singleton
class AiChatSessionDataSource @Inject constructor() {
    private val _state = MutableStateFlow(AiChatSession())
    val state: StateFlow<AiChatSession> = _state.asStateFlow()

    private var nextLocalId = -1L
    private var sessionGeneration = 0L

    /** Replaces the session with server history; only fills an empty, un-hydrated session. */
    @Synchronized
    fun hydrate(messages: List<AiChatMessage>) {
        _state.update { current ->
            if (current.isHydrated || current.messages.isNotEmpty()) {
                current.copy(isHydrated = true)
            } else {
                current.copy(messages = messages, isHydrated = true)
            }
        }
    }

    @Synchronized
    fun beginQuestion(content: AiChatContent.UserText): Long {
        val generation = sessionGeneration
        _state.update { current ->
            // Reuse the trailing user bubble on retries: after a failed send the
            // optimistic message is still the last one, and appending it again
            // would duplicate it on every "try again".
            val lastQuestion = current.messages
                .lastOrNull()
                ?.content as? AiChatContent.UserText
            val messages = if (lastQuestion == content) {
                current.messages
            } else {
                current.messages + AiChatMessage(
                    id = nextLocalId--,
                    sender = AiChatSender.USER,
                    content = content,
                )
            }
            current.copy(messages = messages, isResponding = true)
        }
        return generation
    }

    @Synchronized
    fun completeAnswer(
        generation: Long,
        serverMessageId: Long?,
        response: AiChatContent.AssistantMessage,
    ) {
        if (generation != sessionGeneration) return
        _state.update { current ->
            current.copy(
                messages = current.messages + AiChatMessage(
                    id = serverMessageId ?: nextLocalId--,
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

    /** New chat: the cleared session is by definition hydrated-empty. */
    @Synchronized
    fun reset() {
        sessionGeneration += 1L
        nextLocalId = -1L
        _state.value = AiChatSession(isHydrated = true)
    }
}
