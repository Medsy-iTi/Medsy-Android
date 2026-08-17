package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEgyptPhoneUseCase: ValidateEgyptPhoneUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) {
    suspend operator fun invoke(params: RegisterParams): EmptyMedsyResult<MedsyError> {
        if (params.email.isBlank() || params.password.isBlank() ||
            params.firstName.isBlank() || params.lastName.isBlank() || params.phoneNumber.isBlank()
        ) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }
        if (!validateNameUseCase(params.firstName) || !validateNameUseCase(params.lastName)) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_NAME)
        }
        if (!validateEgyptPhoneUseCase(params.phoneNumber)) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_PHONE_NUMBER)
        }
        if (!validateEmailUseCase(params.email)) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_EMAIL)
        }
        val passwordError = validatePasswordUseCase(params.password)
        if (passwordError != null) {
            return MedsyResult.Error(passwordError)
        }
        return repository.register(params)
    }
}
