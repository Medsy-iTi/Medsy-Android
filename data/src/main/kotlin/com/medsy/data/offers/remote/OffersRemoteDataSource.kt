package com.medsy.data.offers.remote

import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

import com.medsy.data.BuildConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

class OffersRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) {

    suspend fun acceptOffer(requestId: Long, selectedItems: List<SelectedRequestItemDto>): MedsyResult<ConfirmRequestResponseDto, MedsyError.Remote> {
        return safeApiCall {
            apiService.confirmRequest(requestId, ConfirmRequestDto(selectedItems))
        }
    }

    suspend fun getRequestResult(requestId: Long): MedsyResult<RequestResultDto, MedsyError.Remote> =
        safeApiCall { apiService.getRequestResult(requestId) }

    fun streamRequestResult(requestId: Long): Flow<RequestResultDto> = callbackFlow {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/v1/requests/$requestId/stream")
            .header("Accept", "text/event-stream")
            .build()
            
        val sseClient = okHttpClient.newBuilder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()
            
        val factory = EventSources.createFactory(sseClient)
        val adapter = moshi.adapter(RequestResultDto::class.java)

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                try {
                    val resultDto = adapter.fromJson(data)
                    if (resultDto != null) {
                        trySend(resultDto)
                    }
                } catch (_: Exception) {
                }
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                close(t)
            }
        }

        val eventSource = factory.newEventSource(request, listener)
        
        awaitClose {
            eventSource.cancel()
        }
    }
}
