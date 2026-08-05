package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.model.InteractionWarning
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class GetCartInteractionsUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(): MedsyResult<List<InteractionWarning>, MedsyError.Remote> =
        repository.getCartInteractions()
}
