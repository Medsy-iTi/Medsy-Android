package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.DomainError
import com.medsy.domain.common.DomainResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEgyptPhoneUseCase: ValidateEgyptPhoneUseCase
) {
    suspend operator fun invoke(params: RegisterParams): DomainResult<Unit> {
        if (params.email.isBlank() || params.password.isBlank() || params.firstName.isBlank() || params.lastName.isBlank() || params.phoneNumber.isBlank()) {
            return DomainResult.Error(DomainError.Api("All fields are required"))
        }
        if (!validateEgyptPhoneUseCase(params.phoneNumber)) {
            return DomainResult.Error(DomainError.Api("Enter a valid Egyptian phone number"))
        }
        return repository.register(params)
    }
}
