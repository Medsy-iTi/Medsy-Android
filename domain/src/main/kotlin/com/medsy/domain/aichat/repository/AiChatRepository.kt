package com.medsy.domain.aichat.repository

import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.model.AiChatSendResult
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import kotlinx.coroutines.flow.Flow

interface AiChatRepository {
    fun observeSession(): Flow<AiChatSession>

    /** Hydrates the session from the server conversation history. */
    suspend fun loadHistory(): EmptyMedsyResult<MedsyError.Remote>

    /**
     * Sends a text or image message. The resulting messages flow through the
     * observed session; the returned value contains one-shot instructions
     * that must not be reconstructed from conversation history.
     */
    suspend fun send(
        message: AiChatOutgoingMessage,
    ): MedsyResult<AiChatSendResult, MedsyError.Remote>

    /** Deletes the server-side history and clears the local session. */
    suspend fun startNewChat(): EmptyMedsyResult<MedsyError.Remote>
}
