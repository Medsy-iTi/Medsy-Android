package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): MedsyResult<AuthSession, MedsyError> {
        if (email.isBlank() || password.isBlank()) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }
        return repository.login(email.trim(), password)
    }
}
