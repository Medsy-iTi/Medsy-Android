package com.medsy.domain.common.preferences.usecase

import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.repository.UserPreferencesRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(themeMode: ThemeMode) = repository.setThemeMode(themeMode)
}
