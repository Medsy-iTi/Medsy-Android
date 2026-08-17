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
        INVALID_REMINDER_DETAILS,
        INVALID_NAME,
        INVALID_EMAIL,
        INVALID_PASSWORD_MAX_LENGTH,
        INVALID_PASSWORD_SPACES,
        INVALID_PASSWORD_MISSING_LETTER,
    }

    enum class Local : MedsyError {
        MEDIA,
        DATABASE_CONSTRAINT,
        DATABASE_FULL,
        DATABASE_CORRUPT,
        DATABASE_IO,
        DATABASE_ERROR,
        UNKNOWN,
    }
}
