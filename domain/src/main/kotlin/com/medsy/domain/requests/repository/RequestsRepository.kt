package com.medsy.domain.requests.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.requests.model.MedicineRequestDetails

interface RequestsRepository {
    suspend fun getRequestById(id: Long): MedsyResult<MedicineRequestDetails, MedsyError.Remote>
}
