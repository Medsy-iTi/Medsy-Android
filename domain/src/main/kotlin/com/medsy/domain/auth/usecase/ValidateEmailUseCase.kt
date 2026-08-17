package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    private val regex = Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+${'$'}""")

    operator fun invoke(email: String): Boolean {
        if (email != email.trim()) return false
        if (email.contains("..")) return false
        val atCount = email.count { it == '@' }
        if (atCount != 1) return false
        return regex.matches(email)
    }
}
