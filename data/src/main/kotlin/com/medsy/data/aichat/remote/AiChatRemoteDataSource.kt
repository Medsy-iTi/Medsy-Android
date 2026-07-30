package com.medsy.data.aichat.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class AiChatRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun askCatalog(
        request: CatalogQuestionRequestDto,
    ): MedsyResult<CatalogAnswerDto, MedsyError.Remote> =
        safeApiCall { apiService.askCatalog(request) }
}
