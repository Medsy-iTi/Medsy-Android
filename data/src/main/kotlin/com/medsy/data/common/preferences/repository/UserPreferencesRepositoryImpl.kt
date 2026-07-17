package com.medsy.data.common.preferences.repository

import com.medsy.data.common.preferences.local.UserPreferencesLocalDataSource
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.model.UserPreferences
import com.medsy.domain.common.preferences.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val localDataSource: UserPreferencesLocalDataSource,
) : UserPreferencesRepository {
    
    private companion object {
        const val THEME_MODE_SYSTEM = "system"
        const val THEME_MODE_LIGHT = "light"
        const val THEME_MODE_DARK = "dark"
    }

    override val preferences: Flow<UserPreferences> = kotlinx.coroutines.flow.combine(
        localDataSource.themeMode,
        localDataSource.onboardingCompleted
    ) { storedMode, completed ->
        UserPreferences(
            themeMode = storedMode.toThemeMode(),
            hasCompletedOnboarding = completed
        )
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        localDataSource.setThemeMode(themeMode.toStorageValue())
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        localDataSource.setOnboardingCompleted(completed)
    }

    private fun String?.toThemeMode(): ThemeMode = when (this) {
        THEME_MODE_LIGHT -> ThemeMode.Light
        THEME_MODE_DARK -> ThemeMode.Dark
        THEME_MODE_SYSTEM -> ThemeMode.System
        else -> ThemeMode.System
    }

    private fun ThemeMode.toStorageValue(): String = when (this) {
        ThemeMode.System -> THEME_MODE_SYSTEM
        ThemeMode.Light -> THEME_MODE_LIGHT
        ThemeMode.Dark -> THEME_MODE_DARK
    }


}
