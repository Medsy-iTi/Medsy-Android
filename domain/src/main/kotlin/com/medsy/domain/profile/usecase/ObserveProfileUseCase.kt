package com.medsy.domain.profile.usecase

import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<Profile?> = repository.observeProfile()
}
