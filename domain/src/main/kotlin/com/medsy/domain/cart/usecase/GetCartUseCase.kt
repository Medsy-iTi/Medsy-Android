package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(): MedsyResult<Cart, MedsyError.Remote> = repository.getCart()
}