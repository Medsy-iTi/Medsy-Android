package com.medsy.presentation.common.util

import androidx.annotation.StringRes
import com.medsy.domain.common.DomainError
import com.medsy.presentation.R
@StringRes
fun DomainError.toMessageRes(): Int = when (this) {
    is DomainError.Network -> R.string.error_network
    is DomainError.Api     -> resolveApiError(message, code)
    is DomainError.Unknown -> R.string.error_generic
}

@StringRes
private fun resolveApiError(message: String, code: Int?): Int {
    if (code == 409) {
        if (message.contains("phone", ignoreCase = true)) return R.string.error_phone_exists
        if (message.contains("email", ignoreCase = true)) return R.string.error_email_exists
    }

    return when (code) {
        409  -> R.string.error_conflict
        401  -> R.string.error_unauthorized
        403  -> R.string.error_forbidden
        404  -> R.string.error_not_found
        500,
        502,
        503  -> R.string.error_server
        else -> R.string.error_generic
    }
}
