package com.medsy.presentation.common.util

import androidx.annotation.StringRes
import com.medsy.domain.common.MedsyError
import com.medsy.presentation.R

@StringRes
fun MedsyError.toMessageRes(): Int = when (this) {
    is MedsyError.Remote -> this.resolveRemoteError()

    is MedsyError.Validation -> this.resolveValidationError()

    is MedsyError.Local -> this.resolveLocalError()
}

@StringRes
private fun MedsyError.Local.resolveLocalError(): Int = when (this) {
    MedsyError.Local.MEDIA -> R.string.prescription_media_error
    MedsyError.Local.DATABASE_CONSTRAINT -> R.string.error_database_constraint
    MedsyError.Local.DATABASE_FULL -> R.string.error_database_full
    MedsyError.Local.DATABASE_CORRUPT -> R.string.error_database_corrupt
    MedsyError.Local.DATABASE_IO -> R.string.error_database_io
    MedsyError.Local.DATABASE_ERROR,
    MedsyError.Local.UNKNOWN -> R.string.error_generic
}

@StringRes
private fun MedsyError.Validation.resolveValidationError(): Int = when (this) {
    MedsyError.Validation.REQUIRED_FIELDS                  -> R.string.auth_error_required_field
    MedsyError.Validation.INVALID_PHONE_NUMBER             -> R.string.error_phone_invalid
    MedsyError.Validation.INVALID_OTP                      -> R.string.auth_invalid_code
    MedsyError.Validation.INVALID_REMINDER_DETAILS         -> R.string.reminder_invalid_details
    MedsyError.Validation.INVALID_NAME                     -> R.string.auth_error_invalid_name
    MedsyError.Validation.INVALID_EMAIL                    -> R.string.auth_error_invalid_email
    MedsyError.Validation.INVALID_PASSWORD_MAX_LENGTH      -> R.string.auth_error_password_max_15
    MedsyError.Validation.INVALID_PASSWORD_SPACES          -> R.string.auth_error_password_spaces
    MedsyError.Validation.INVALID_PASSWORD_MISSING_LETTER  -> R.string.auth_error_password_letters_required
}

@StringRes
private fun MedsyError.Remote.resolveRemoteError(): Int = when (this) {
    MedsyError.Remote.NoInternet -> R.string.error_network
    MedsyError.Remote.RequestTimeout -> R.string.error_request_timeout
    MedsyError.Remote.Serialization -> R.string.error_serialization
    MedsyError.Remote.EmptyResponse -> R.string.error_empty_response
    MedsyError.Remote.Unknown -> R.string.error_generic
    is MedsyError.Remote.Http -> resolveHttpError(statusCode, serverMessage)
}


@StringRes
private fun resolveHttpError(statusCode: Int, serverMessage: String?): Int {
    if (statusCode == 400 || statusCode == 409) {
        when {
            serverMessage.containsAny(
                "phone already",
                "phone is already",
                "phone number already",
                "phone number is already",
            ) ->
                return R.string.error_phone_exists

            serverMessage.containsAny("email already", "email is already") ->
                return R.string.error_email_exists

            serverMessage.containsAny("invalid email or password", "invalid credentials") ->
                return R.string.error_unauthorized

            serverMessage.containsAny("otp", "verification code") ->
                return R.string.auth_invalid_code
        }
    }

    return when (statusCode) {
        400 -> R.string.error_bad_request
        401 -> R.string.error_unauthorized
        403 -> R.string.error_forbidden
        404 -> R.string.error_not_found
        408 -> R.string.error_request_timeout
        409 -> R.string.error_conflict
        413 -> R.string.error_payload_too_large
        429 -> R.string.error_too_many_requests
        in 500..599 -> R.string.error_server
        else -> R.string.error_generic
    }
}

private fun String?.containsAny(vararg values: String): Boolean =
    this != null && values.any { contains(it, ignoreCase = true) }
