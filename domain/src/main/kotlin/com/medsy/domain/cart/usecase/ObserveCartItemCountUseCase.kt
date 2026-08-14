package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartItemCountUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    operator fun invoke(): Flow<Int> = repository.cartItemCount
}
