package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(): EmptyMedsyResult<MedsyError> =
        when (val remoteResult = repository.clearCart()) {
            is MedsyResult.Error -> remoteResult
            is MedsyResult.Success -> when (val localResult = repository.clearDraft()) {
                is MedsyResult.Error -> localResult
                is MedsyResult.Success -> MedsyResult.Success(Unit)
            }
        }
}