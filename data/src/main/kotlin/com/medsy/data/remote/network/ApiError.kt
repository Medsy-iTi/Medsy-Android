package com.medsy.data.remote.network

sealed class ApiError {

    data object NoInternet : ApiError()

    data object EmptyResponse : ApiError()

    data class Server(
        val code: Int,
        val message: String
    ) : ApiError()

    data class Unknown(
        val message: String? = null
    ) : ApiError()
}