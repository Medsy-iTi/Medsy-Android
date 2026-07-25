package com.medsy.data.aichat.repository

import com.medsy.data.aichat.mapper.toDomain
import com.medsy.data.aichat.mock.MockAiChatDataSource
import com.medsy.domain.aichat.model.AiChatAction
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.aichat.repository.AiChatRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val dataSource: MockAiChatDataSource,
) : AiChatRepository {
    override fun observeSession(): Flow<AiChatSession> =
        dataSource.state.map { it.toDomain() }

    override suspend fun submit(
        action: AiChatAction,
    ): EmptyMedsyResult<MedsyError.Local> = dataSource.submit(action)

    override fun resetSession() = dataSource.reset()
}
