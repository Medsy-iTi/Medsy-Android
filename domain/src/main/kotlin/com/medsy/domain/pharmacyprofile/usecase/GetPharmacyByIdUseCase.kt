package com.medsy.domain.pharmacyprofile.usecase

import com.medsy.domain.pharmacyprofile.repository.PharmacyProfileRepository
import javax.inject.Inject

class GetPharmacyByIdUseCase @Inject constructor(
    private val repository: PharmacyProfileRepository,
) {
    suspend operator fun invoke(pharmacyId: Long) = repository.getPharmacyById(pharmacyId)
}
