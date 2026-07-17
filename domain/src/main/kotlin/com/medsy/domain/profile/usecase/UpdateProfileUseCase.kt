package com.medsy.domain.profile.usecase

import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(
        homeAddress: String?,
        dob: String?,
    ): Result<Profile> = profileRepository.updateCurrentProfile(
        homeAddress = homeAddress,
        dob = dob,
    )
}
