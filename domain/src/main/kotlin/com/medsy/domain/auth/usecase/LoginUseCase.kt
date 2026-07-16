package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): DomainResult<AuthSession> {
        if (email.isBlank() || password.isBlank()) {
            return DomainResult.Error(DomainError.Api("Email and password are required"))
        }
        return repository.login(email.trim(), password)
    }
}
