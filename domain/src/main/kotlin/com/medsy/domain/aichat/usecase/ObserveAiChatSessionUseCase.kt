package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.aichat.repository.AiChatRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAiChatSessionUseCase @Inject constructor(
    private val repository: AiChatRepository,
) {
    operator fun invoke(): Flow<AiChatSession> = repository.observeSession()
}
