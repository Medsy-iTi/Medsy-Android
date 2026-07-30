package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.repository.AiChatRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class SubmitAiChatActionUseCase @Inject constructor(
    private val repository: AiChatRepository,
) {
    suspend operator fun invoke(
        action: AiChatAction,
    ): EmptyMedsyResult<MedsyError.Remote> = repository.submit(action)
}
