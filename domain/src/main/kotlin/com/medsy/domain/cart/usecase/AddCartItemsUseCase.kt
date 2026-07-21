package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.model.AddCartItemsOutcome
import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.model.CartItemInput
import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import javax.inject.Inject

class AddCartItemsUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        items: List<CartItemInput>,
    ): MedsyResult<AddCartItemsOutcome, MedsyError.Remote> {
        if (items.isEmpty()) {
            return when (val result = repository.getCart()) {
                is MedsyResult.Error -> result
                is MedsyResult.Success ->
                    MedsyResult.Success(AddCartItemsOutcome.Complete(result.data))
            }
        }

        var latestCart: Cart? = null
        var addedItemsCount = 0

        for (item in items) {
            when (val result = repository.addItem(item.productId, item.quantity)) {
                is MedsyResult.Success -> {
                    latestCart = result.data
                    addedItemsCount += 1
                }

                is MedsyResult.Error -> {
                    val cart = latestCart
                    return if (cart == null) {
                        result
                    } else {
                        MedsyResult.Success(
                            AddCartItemsOutcome.Partial(
                                cart = cart,
                                addedItemsCount = addedItemsCount,
                            )
                        )
                    }
                }
            }
        }

        return latestCart?.let {
            MedsyResult.Success(AddCartItemsOutcome.Complete(it))
        } ?: repository.getCart().map {
            AddCartItemsOutcome.Complete(it)
        }
    }
}
