package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String): DomainResult<AuthSession> {
        val cleanCode = otpCode.trim()
        if (cleanCode.length != 6 || !cleanCode.all { it.isDigit() }) {
            return DomainResult.Error(DomainError.Api("OTP must be 6 digits"))
        }
        return repository.verifyOtp(email, cleanCode)
    }
}
