package com.medsy.domain.common.preferences.usecase

import com.medsy.domain.common.preferences.model.UserPreferences
import com.medsy.domain.common.preferences.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreferences> = repository.preferences
}
