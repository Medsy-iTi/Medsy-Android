package com.medsy.data.common.preferences.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    val themeMode: Flow<String?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[THEME_MODE_KEY] }

    suspend fun setThemeMode(themeMode: String) {
        try {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = themeMode
            }
        } catch (_: IOException) {
            // Keep the last successfully stored preference when storage is unavailable.
        }
    }


}
