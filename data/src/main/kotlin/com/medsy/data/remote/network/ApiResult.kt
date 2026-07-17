package com.medsy.data.remote.network

sealed class ApiResult<out T> {

    data class Success<T>(
        val data: T?,
        val message: String
    ) : ApiResult<T>()

    data class Error(
       val error : ApiError
    ) : ApiResult<Nothing>()
}