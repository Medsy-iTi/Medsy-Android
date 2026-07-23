package com.medsy.domain.profile.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.model.UpdateProfileParams
import com.medsy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(
        params: UpdateProfileParams,
    ): MedsyResult<Profile, MedsyError> {
        val normalizedParams = params.copy(
            firstName = params.firstName.trim(),
            lastName = params.lastName.trim(),
        )
        if (normalizedParams.firstName.isBlank() || normalizedParams.lastName.isBlank()) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }
        return profileRepository.updateCurrentProfile(normalizedParams)
    }
}
