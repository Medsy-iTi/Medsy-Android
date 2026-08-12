package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.model.AiChatMessageAction
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.repository.AiChatRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class SendAiChatMessageUseCase @Inject constructor(
    private val repository: AiChatRepository,
) {
    suspend operator fun invoke(
        message: AiChatOutgoingMessage,
    ): MedsyResult<AiChatMessageAction?, MedsyError.Remote> =
        repository.send(message)
}
