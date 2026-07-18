package com.medsy.data.remote.network

import android.util.Log
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "SafeRestCall"

private val errorAdapter = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
    .adapter(MedsyApiErrorResponse::class.java)

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>,
): MedsyResult<T, MedsyError.Remote> =
    executeRestCall(apiCall) { body ->
        body.data
            ?.let { MedsyResult.Success(it) }
            ?: MedsyResult.Error(MedsyError.Remote.EmptyResponse)
    }

suspend fun <T> safeEmptyRestCall(
    apiCall: suspend () -> Response<ApiResponse<T>>,
): EmptyMedsyResult<MedsyError.Remote> =
    executeRestCall(apiCall) {
        MedsyResult.Success(Unit)
    }

private suspend fun <T, R> executeRestCall(
    apiCall: suspend () -> Response<ApiResponse<T>>,
    successResult: (ApiResponse<T>) -> MedsyResult<R, MedsyError.Remote>,
): MedsyResult<R, MedsyError.Remote> =
    try {
        val response = apiCall()
        val body = response.body()

        when {
            !response.isSuccessful -> MedsyResult.Error(
                MedsyError.Remote.Http(
                    statusCode = response.code(),
                    serverMessage = response.errorBody()
                        ?.string()
                        ?.let(::parseServerMessage)
                        ?: response.message().takeIf(String::isNotBlank),
                )
            )

            body == null -> MedsyResult.Error(MedsyError.Remote.EmptyResponse)

            !body.success -> MedsyResult.Error(
                MedsyError.Remote.Http(
                    statusCode = response.code(),
                    serverMessage = body.message,
                )
            )

            else -> successResult(body)
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: SocketTimeoutException) {
        Log.e(TAG, "REST request timed out", error)
        MedsyResult.Error(MedsyError.Remote.RequestTimeout)
    } catch (error: JsonDataException) {
        Log.e(TAG, "REST response did not match the expected schema", error)
        MedsyResult.Error(MedsyError.Remote.Serialization)
    } catch (error: JsonEncodingException) {
        Log.e(TAG, "REST response contained malformed JSON", error)
        MedsyResult.Error(MedsyError.Remote.Serialization)
    } catch (error: IOException) {
        Log.e(TAG, "REST request failed because the network is unavailable", error)
        MedsyResult.Error(MedsyError.Remote.NoInternet)
    } catch (error: Exception) {
        Log.e(TAG, "Unexpected REST failure", error)
        MedsyResult.Error(MedsyError.Remote.Unknown)
    }

private fun parseServerMessage(rawBody: String): String? = try {
    errorAdapter.fromJson(rawBody)?.message
} catch (_: Exception) {
    null
}
