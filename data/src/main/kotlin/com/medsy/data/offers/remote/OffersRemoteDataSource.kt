package com.medsy.data.offers.remote

import com.medsy.data.BuildConfig
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class OffersRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    @Named("SseClient") private val sseClient: OkHttpClient,
    moshi: Moshi,
) {
    private val snapshotAdapter = moshi.adapter(RequestResultDto::class.java)
    private val updateAdapter = moshi.adapter(RequestResultUpdateDto::class.java)

    suspend fun selectItems(
        requestId: Long,
        selectedItems: List<SelectedItemDto>,
    ): MedsyResult<SelectionResponseDto, MedsyError.Remote> = safeApiCall {
        apiService.selectRequestItems(requestId, ConfirmRequestDto(selectedItems))
    }

    suspend fun confirmFulfillment(
        requestId: Long,
        fulfillmentMethod: String,
    ): MedsyResult<FulfillmentConfirmationDto, MedsyError.Remote> = safeApiCall {
        apiService.confirmFulfillment(requestId, FulfillmentRequestDto(fulfillmentMethod))
    }

    suspend fun getRequestResult(
        requestId: Long,
    ): MedsyResult<RequestResultDto, MedsyError.Remote> =
        safeApiCall { apiService.getRequestResult(requestId) }

    fun streamRequestResult(requestId: Long): Flow<RequestStreamEventDto> = callbackFlow {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/v1/requests/$requestId/stream")
            .header("Accept", "text/event-stream")
            .build()
        var receivedTerminalEvent = false

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                try {
                    when (type) {
                        "snapshot" -> snapshotAdapter.fromJson(data)
                            ?.let { trySend(RequestStreamEventDto.Snapshot(it)) }

                        "request-item-updated" -> updateAdapter.fromJson(data)
                            ?.let { trySend(RequestStreamEventDto.ItemsUpdated(it)) }

                        "stream-closed" -> {
                            receivedTerminalEvent = true
                            trySend(RequestStreamEventDto.Closed(data.trim()))
                            close()
                        }
                    }
                } catch (error: Exception) {
                    close(error)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                if (receivedTerminalEvent) close() else close(IOException("SSE stream closed"))
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?,
            ) {
                close(
                    response?.let { SseHttpException(it.code, it.message) }
                        ?: t
                        ?: IOException("SSE stream failed")
                )
            }
        }

        val eventSource = EventSources.createFactory(sseClient).newEventSource(request, listener)
        awaitClose { eventSource.cancel() }
    }
}

internal class SseHttpException(
    val statusCode: Int,
    message: String?,
) : IOException(message)
