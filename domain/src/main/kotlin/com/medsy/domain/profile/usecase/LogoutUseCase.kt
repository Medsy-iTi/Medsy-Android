package com.medsy.domain.profile.usecase

import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.cart.usecase.ClearCartDraftUseCase
import com.medsy.domain.requests.usecase.ClearActiveRequestsUseCase
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val clearCartDraft: ClearCartDraftUseCase,
    private val clearActiveRequests: ClearActiveRequestsUseCase,
) {
    suspend operator fun invoke(): EmptyMedsyResult<MedsyError.Remote> {
        val result = authRepository.logout()
        clearCartDraft()
        clearActiveRequests()
        return result
    }
}
