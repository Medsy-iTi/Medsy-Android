package com.medsy.data.aichat.remote

import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AiChatRemoteDataSource @Inject constructor(
    private val aiService: AiService,
) {
    suspend fun sendMessage(
        request: ChatMessageRequestDto,
    ): MedsyResult<ChatMessageResponseDto, MedsyError.Remote> =
        safeApiCall { aiService.sendChatMessage(request) }

    suspend fun sendImageMessage(
        image: MultipartBody.Part,
        message: RequestBody?,
    ): MedsyResult<ChatMessageResponseDto, MedsyError.Remote> =
        safeApiCall { aiService.sendChatImageMessage(image, message) }

    suspend fun getHistory(): MedsyResult<ChatHistoryResponseDto, MedsyError.Remote> =
        safeApiCall { aiService.getChatHistory() }

    suspend fun deleteHistory(): MedsyResult<ChatHistoryResponseDto, MedsyError.Remote> =
        safeApiCall { aiService.deleteChatHistory() }
}
