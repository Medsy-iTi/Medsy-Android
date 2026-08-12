package com.medsy.domain.requests.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.requests.model.MedicineRequest
import com.medsy.domain.requests.model.MedicineRequestPage

interface RequestsRepository {
    suspend fun getRequestById(id: Long): MedsyResult<MedicineRequest, MedsyError.Remote>

    suspend fun getRequests(
        page: Int,
        size: Int,
        sort: List<String>,
    ): MedsyResult<MedicineRequestPage, MedsyError.Remote>
}
