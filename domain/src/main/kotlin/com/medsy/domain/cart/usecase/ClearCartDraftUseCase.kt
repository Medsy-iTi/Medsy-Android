package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class ClearCartDraftUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(): EmptyMedsyResult<MedsyError.Local> = repository.clearDraft()
}