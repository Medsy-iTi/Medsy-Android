package com.medsy.data.aichat.repository

import com.medsy.data.aichat.local.AiChatSessionDataSource
import com.medsy.data.aichat.mapper.toDomain
import com.medsy.data.aichat.remote.AiChatRemoteDataSource
import com.medsy.data.aichat.remote.CatalogQuestionRequestDto
import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.model.AiChatLanguage
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.aichat.repository.AiChatRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.onError
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiChatRemoteDataSource,
    private val sessionDataSource: AiChatSessionDataSource,
) : AiChatRepository {
    override fun observeSession(): Flow<AiChatSession> = sessionDataSource.state

    override suspend fun submit(
        action: AiChatAction,
    ): EmptyMedsyResult<MedsyError.Remote> {
        if (sessionDataSource.state.value.isResponding) {
            return MedsyResult.Success(Unit)
        }

        val sessionGeneration = sessionDataSource.beginQuestion(action.question)
        return remoteDataSource.askCatalog(
            CatalogQuestionRequestDto(
                question = action.question,
                lang = when (action.language) {
                    AiChatLanguage.ENGLISH -> "en"
                    AiChatLanguage.ARABIC -> "ar"
                },
                limit = action.limit,
            )
        ).map { response ->
            sessionDataSource.completeAnswer(
                generation = sessionGeneration,
                response = response.toDomain(),
            )
        }.onError {
            sessionDataSource.failQuestion(sessionGeneration)
        }
    }

    override fun resetSession() = sessionDataSource.reset()
}
