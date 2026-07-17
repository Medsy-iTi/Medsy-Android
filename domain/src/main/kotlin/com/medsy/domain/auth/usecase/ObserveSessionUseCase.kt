package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthSession?> {
        return repository.observeSession()
    }
}
