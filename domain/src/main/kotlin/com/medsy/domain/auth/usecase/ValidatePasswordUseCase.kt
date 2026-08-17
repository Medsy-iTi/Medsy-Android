package com.medsy.domain.auth.usecase

import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    companion object {
        const val MIN_LENGTH = 6
        const val MAX_LENGTH = 15
    }

    /**
     * Returns null when the password is valid, or the specific [MedsyError.Validation]
     * that describes the first violation found.
     */
    operator fun invoke(password: String): MedsyError.Validation? = when {
        password.isBlank()               -> MedsyError.Validation.REQUIRED_FIELDS
        password.contains(' ')           -> MedsyError.Validation.INVALID_PASSWORD_SPACES
        password.length < MIN_LENGTH     -> MedsyError.Validation.REQUIRED_FIELDS   // re-use "min 6" path — ViewModel maps it
        password.length > MAX_LENGTH     -> MedsyError.Validation.INVALID_PASSWORD_MAX_LENGTH
        !password.any { it.isLetter() } -> MedsyError.Validation.INVALID_PASSWORD_MISSING_LETTER
        else                             -> null
    }
}
