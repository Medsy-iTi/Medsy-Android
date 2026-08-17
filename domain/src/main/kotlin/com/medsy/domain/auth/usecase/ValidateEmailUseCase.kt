package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    // RFC-compliant enough for registration: requires local@domain.tld
    // Rejects leading/trailing spaces, double dots, multiple @, missing domain.
    private val regex = Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+${'$'}""")

    operator fun invoke(email: String): Boolean {
        if (email != email.trim()) return false        // leading or trailing spaces
        if (email.contains("..")) return false         // consecutive dots
        val atCount = email.count { it == '@' }
        if (atCount != 1) return false                 // must have exactly one @
        return regex.matches(email)
    }
}
