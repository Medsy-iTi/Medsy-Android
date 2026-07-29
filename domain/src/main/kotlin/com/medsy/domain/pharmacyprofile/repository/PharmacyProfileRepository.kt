package com.medsy.domain.pharmacyprofile.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacyprofile.model.PharmacyProfile

interface PharmacyProfileRepository {
    suspend fun getPharmacyById(
        pharmacyId: Long,
    ): MedsyResult<PharmacyProfile, MedsyError.Remote>
}
