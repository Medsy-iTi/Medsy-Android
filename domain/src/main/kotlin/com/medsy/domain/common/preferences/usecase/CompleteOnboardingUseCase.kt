package com.medsy.domain.common.preferences.usecase

import com.medsy.domain.common.preferences.repository.UserPreferencesRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        userPreferencesRepository.setOnboardingCompleted(true)
    }
}
