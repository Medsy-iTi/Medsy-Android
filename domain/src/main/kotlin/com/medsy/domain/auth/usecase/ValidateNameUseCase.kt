package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateNameUseCase @Inject constructor() {
    // Allows Arabic and English letters, single internal spaces, and hyphens.
    // Rejects digits, special characters, and leading/trailing spaces.
    private val regex = Regex("""^[\p{L}][\p{L} '-]*[\p{L}]${'$'}|^[\p{L}]${'$'}""")

    operator fun invoke(name: String): Boolean {
        if (name != name.trim()) return false          // leading or trailing spaces
        if (name.isBlank()) return false
        return regex.matches(name)
    }
}
