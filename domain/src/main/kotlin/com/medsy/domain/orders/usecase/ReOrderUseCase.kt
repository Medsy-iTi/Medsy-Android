package com.medsy.domain.orders.usecase

import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class ReOrderUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(items: List<CartItemInput>): EmptyMedsyResult<MedsyError.Remote> {
        return repository.addItemsBulk(items)
    }

}