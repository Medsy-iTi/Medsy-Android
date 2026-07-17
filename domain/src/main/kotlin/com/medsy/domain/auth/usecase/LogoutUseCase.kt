package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        return repository.logout()
    }
}
