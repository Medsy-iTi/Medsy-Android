package com.medsy.data.offers.repository

import com.medsy.data.offers.mapper.toDomain
import com.medsy.data.offers.remote.OffersRemoteDataSource
import com.medsy.data.offers.remote.RequestStreamEventDto
import com.medsy.data.offers.remote.SelectedItemDto
import com.medsy.data.offers.remote.SseHttpException
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.offers.model.FulfillmentConfirmation
import com.medsy.domain.offers.model.RequestResult
import com.medsy.domain.offers.model.RequestResultEvent
import com.medsy.domain.offers.model.SelectedOfferItem
import com.medsy.domain.offers.model.SelectionDraft
import com.medsy.domain.offers.repository.OffersRepository
import com.medsy.domain.orders.model.FulfillmentMethod
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class OffersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OffersRemoteDataSource,
) : OffersRepository {
    override suspend fun selectItems(
        requestId: Long,
        selectedItems: List<SelectedOfferItem>,
    ): MedsyResult<SelectionDraft, MedsyError.Remote> =
        remoteDataSource.selectItems(
            requestId,
            selectedItems.map { SelectedItemDto(it.requestItemId, it.productId) },
        ).map { it.toDomain() }

    override suspend fun confirmFulfillment(
        requestId: Long,
        fulfillmentMethod: FulfillmentMethod,
    ): MedsyResult<FulfillmentConfirmation, MedsyError.Remote> =
        remoteDataSource.confirmFulfillment(requestId, fulfillmentMethod.name)
            .map { it.toDomain() }

    override suspend fun getRequestResult(
        requestId: Long,
    ): MedsyResult<RequestResult, MedsyError.Remote> =
        remoteDataSource.getRequestResult(requestId).map { it.toDomain() }

    override fun streamRequestResult(
        requestId: Long,
    ): Flow<MedsyResult<RequestResultEvent, MedsyError.Remote>> =
        remoteDataSource.streamRequestResult(requestId)
            .retryWhen { error, attempt ->
                val retryable = error is IOException &&
                        (error !is SseHttpException || error.statusCode >= 500)
                if (retryable) {
                    val reconnectDelay = if (attempt >= 4) {
                        15_000L
                    } else {
                        1_000L shl attempt.toInt()
                    }
                    delay(reconnectDelay.milliseconds)
                    true
                } else {
                    false
                }
            }
            .map<RequestStreamEventDto, MedsyResult<RequestResultEvent, MedsyError.Remote>> {
                MedsyResult.Success(it.toDomain())
            }
            .catch { error ->
                if (error is CancellationException) throw error
                emit(MedsyResult.Error(error.toRemoteError()))
            }

    private fun Throwable.toRemoteError(): MedsyError.Remote = when (this) {
        is SseHttpException -> MedsyError.Remote.Http(statusCode, message)
        is SocketTimeoutException -> MedsyError.Remote.RequestTimeout
        is JsonDataException -> MedsyError.Remote.Serialization
        is IOException -> MedsyError.Remote.NoInternet
        else -> MedsyError.Remote.Unknown
    }
}
