package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.MasterOrder
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(id: Long): MedsyResult<MasterOrder, MedsyError.Remote> {
        return ordersRepository.getOrderById(id)
    }
}
