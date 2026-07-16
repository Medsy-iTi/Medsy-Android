package com.medsy.presentation.common.util

import androidx.annotation.StringRes
import com.medsy.domain.common.DomainError
import com.medsy.presentation.R

/**
 * Maps a [DomainError] to a localized string resource ID.
 *
 * Resolution order for [DomainError.Api]:
 *  1. Known server message keywords → specific localized resource (e.g. phone/email conflict)
 *  2. HTTP status code → generic localized resource (409, 401, 5xx, …)
 *  3. Fallback → [R.string.error_generic]
 *
 * String matching is intentionally limited to well-known, stable server messages
 * that cannot be distinguished by HTTP code alone (both "phone exists" and "email
 * exists" return 409). The match is case-insensitive and keyword-based so minor
 * server-side wording changes do not break the mapping silently — they fall through
 * to the HTTP-code bucket instead.
 */
@StringRes
fun DomainError.toMessageRes(): Int = when (this) {
    is DomainError.Network -> R.string.error_network
    is DomainError.Api     -> resolveApiError(message, code)
    is DomainError.Unknown -> R.string.error_generic
}

@StringRes
private fun resolveApiError(message: String, code: Int?): Int {
    // --- Specific 409 sub-cases (server message is the only discriminator) ---
    if (code == 409) {
        if (message.contains("phone", ignoreCase = true)) return R.string.error_phone_exists
        if (message.contains("email", ignoreCase = true)) return R.string.error_email_exists
    }

    // --- HTTP-code-level mapping ---
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
