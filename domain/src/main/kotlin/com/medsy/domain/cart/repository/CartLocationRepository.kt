package com.medsy.domain.cart.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface CartLocationRepository {
    suspend fun getAddress(
        latitude: Double,
        longitude: Double,
    ): MedsyResult<String, MedsyError.Local>
}
