package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.common.DomainResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(params: RegisterParams): DomainResult<Unit>
    suspend fun verifyOtp(email: String, otpCode: String): DomainResult<AuthSession>
    suspend fun login(email: String, password: String): DomainResult<AuthSession>
    suspend fun refreshToken(): DomainResult<AuthSession>
    suspend fun logout(): DomainResult<Unit>
    fun observeSession(): Flow<AuthSession?>
    suspend fun hasValidSession(): Boolean
}
