package com.medsy.domain.common

sealed interface MedsyError {

    sealed interface Remote : MedsyError {
        data object NoInternet : Remote
        data object RequestTimeout : Remote
        data object Serialization : Remote
        data object EmptyResponse : Remote
        data object Unknown : Remote

        data class Http(
            val statusCode: Int,
            val serverMessage: String?,
        ) : Remote
    }

    enum class Validation : MedsyError {
        REQUIRED_FIELDS,
        INVALID_PHONE_NUMBER,
        INVALID_OTP,
    }

    enum class Local : MedsyError {
        MEDIA,
        UNKNOWN,
    }
}
