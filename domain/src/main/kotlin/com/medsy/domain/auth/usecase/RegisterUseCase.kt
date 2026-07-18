package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEgyptPhoneUseCase: ValidateEgyptPhoneUseCase
) {
    suspend operator fun invoke(params: RegisterParams): EmptyMedsyResult<MedsyError> {
        if (params.email.isBlank() || params.password.isBlank() || params.firstName.isBlank() || params.lastName.isBlank() || params.phoneNumber.isBlank()) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }
        if (!validateEgyptPhoneUseCase(params.phoneNumber)) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_PHONE_NUMBER)
        }
        return repository.register(params)
    }
}
