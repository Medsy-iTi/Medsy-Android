package com.medsy.domain.aichat.repository

import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import kotlinx.coroutines.flow.Flow

interface AiChatRepository {
    fun observeSession(): Flow<AiChatSession>

    suspend fun submit(action: AiChatAction): EmptyMedsyResult<MedsyError.Remote>

    fun resetSession()
}
