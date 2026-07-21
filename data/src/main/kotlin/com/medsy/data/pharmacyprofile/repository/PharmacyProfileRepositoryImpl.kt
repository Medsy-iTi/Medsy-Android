package com.medsy.data.pharmacyprofile.repository

import com.medsy.data.pharmacyprofile.mapper.toDomain
import com.medsy.data.pharmacyprofile.remote.PharmacyProfileRemoteDataSource
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.pharmacyprofile.model.PharmacyProfile
import com.medsy.domain.pharmacyprofile.repository.PharmacyProfileRepository
import javax.inject.Inject

class PharmacyProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: PharmacyProfileRemoteDataSource,
) : PharmacyProfileRepository {
    override suspend fun getPharmacyById(
        pharmacyId: Long,
    ): MedsyResult<PharmacyProfile, MedsyError.Remote> =
        remoteDataSource.getPharmacyById(pharmacyId).map { it.toDomain() }
}
