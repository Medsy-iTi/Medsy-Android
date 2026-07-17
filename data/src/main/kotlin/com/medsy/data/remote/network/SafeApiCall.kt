package com.medsy.data.remote.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val errorAdapter = moshi.adapter(ErrorResponse::class.java)

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): ApiResult<T> {

    return try {

        val response = apiCall()

        if (response.isSuccessful) {

            val body = response.body()

            if (body == null) {

                ApiResult.Error(
                    ApiError.EmptyResponse
                )

            } else if (body.success) {

                ApiResult.Success(
                    data = body.data,
                    message = body.message
                )

            } else {

                ApiResult.Error(
                    ApiError.Server(
                        code = response.code(),
                        message = body.message
                    )
                )
            }

        } else {

            val errorMessage = response.errorBody()
                ?.string()
                ?.let { errorAdapter.fromJson(it)?.message }
                ?: response.message()

            ApiResult.Error(
                ApiError.Server(
                    code = response.code(),
                    message = errorMessage
                )
            )
        }

    } catch (e: CancellationException) {

        throw e
    } catch (e: IOException) {
        android.util.Log.e("NetworkError", "IOException: ${e.localizedMessage}", e)
        ApiResult.Error(
            ApiError.NoInternet
        )

    }
    catch (e: Exception) {
        android.util.Log.e("NetworkError", "General Exception: ${e.localizedMessage}", e)
        ApiResult.Error(
            ApiError.Unknown(e.message)
        )
    }


}
