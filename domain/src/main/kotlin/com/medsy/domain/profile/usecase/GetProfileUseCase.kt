package com.medsy.domain.profile.usecase

import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(): Result<Profile> = profileRepository.getCurrentProfile()
}
