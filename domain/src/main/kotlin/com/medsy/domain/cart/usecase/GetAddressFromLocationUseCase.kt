package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.repository.CartLocationRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class GetAddressFromLocationUseCase @Inject constructor(
    private val repository: CartLocationRepository,
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): MedsyResult<String, MedsyError.Local> = repository.getAddress(latitude, longitude)
}
