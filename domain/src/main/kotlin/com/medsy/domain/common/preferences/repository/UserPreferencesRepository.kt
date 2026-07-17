package com.medsy.domain.common.preferences.repository

import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setOnboardingCompleted()
}
