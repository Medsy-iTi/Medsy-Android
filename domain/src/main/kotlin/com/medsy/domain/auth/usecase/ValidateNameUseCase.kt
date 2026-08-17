package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateNameUseCase @Inject constructor() {

    private val regex = Regex("""^[\p{L}][\p{L} '-]*[\p{L}]${'$'}|^[\p{L}]${'$'}""")

    operator fun invoke(name: String): Boolean {
        if (name != name.trim()) return false
        if (name.isBlank()) return false
        return regex.matches(name)
    }
}
