package com.medsy.domain.profile.usecase

import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): EmptyMedsyResult<MedsyError.Remote> =
        authRepository.logout()
}
