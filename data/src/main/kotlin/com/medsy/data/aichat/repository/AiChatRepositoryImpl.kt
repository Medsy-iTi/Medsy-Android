package com.medsy.data.aichat.repository

import com.medsy.data.aichat.local.AiChatSessionDataSource
import com.medsy.data.aichat.mapper.toDomain
import com.medsy.data.aichat.remote.AiChatRemoteDataSource
import com.medsy.data.aichat.remote.ChatMessageRequestDto
import com.medsy.data.common.media.PrescriptionImageStorage
import com.medsy.data.prescription.remote.PrescriptionImageMimeType
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.model.AiChatSendResult
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.aichat.repository.AiChatRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.asEmptyDataResult
import com.medsy.domain.common.map
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiChatRemoteDataSource,
    private val sessionDataSource: AiChatSessionDataSource,
    private val imageStorage: PrescriptionImageStorage,
) : AiChatRepository {

    override fun observeSession(): Flow<AiChatSession> = sessionDataSource.state

    override suspend fun loadHistory(): EmptyMedsyResult<MedsyError.Remote> {
        if (sessionDataSource.state.value.isHydrated) {
            return MedsyResult.Success(Unit)
        }
        return remoteDataSource.getHistory()
            .onSuccess { history ->
                sessionDataSource.hydrate(
                    history.messages.orEmpty().mapNotNull { it.toDomain() },
                )
            }
            .asEmptyDataResult()
    }

    override suspend fun send(
        message: AiChatOutgoingMessage,
    ): MedsyResult<AiChatSendResult, MedsyError.Remote> {
        if (sessionDataSource.state.value.isResponding) {
            return MedsyResult.Success(AiChatSendResult(action = null, reminder = null))
        }

        val userContent = when (message) {
            is AiChatOutgoingMessage.Text -> AiChatContent.UserText(message.message)
            is AiChatOutgoingMessage.Image -> AiChatContent.UserText(
                value = message.caption,
                imageUri = message.image.uri,
            )
        }
        val generation = sessionDataSource.beginQuestion(userContent)

        val result = when (message) {
            is AiChatOutgoingMessage.Text ->
                remoteDataSource.sendMessage(ChatMessageRequestDto(message.message))

            is AiChatOutgoingMessage.Image -> {
                val file = imageStorage.getFile(message.image)
                if (!file.exists()) {
                    sessionDataSource.failQuestion(generation)
                    return MedsyResult.Error(MedsyError.Remote.Unknown)
                }
                val mimeType = PrescriptionImageMimeType.fromExtension(file.extension).value
                val imagePart = MultipartBody.Part.createFormData(
                    "image", file.name, file.asRequestBody(mimeType.toMediaType()),
                )
                val captionPart = message.caption
                    .takeIf(String::isNotBlank)
                    ?.toRequestBody("text/plain".toMediaType())
                remoteDataSource.sendImageMessage(imagePart, captionPart)
            }
        }

        return result
            .map { response ->
                val assistantMessage = response.toDomain()
                sessionDataSource.completeAnswer(
                    generation = generation,
                    serverMessageId = response.messageId,
                    response = assistantMessage,
                )
                AiChatSendResult(
                    action = assistantMessage.action,
                    reminder = assistantMessage.reminder,
                )
            }
            .onError { sessionDataSource.failQuestion(generation) }
    }

    override suspend fun startNewChat(): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.deleteHistory()
            .onSuccess { sessionDataSource.reset() }
            .asEmptyDataResult()
}
