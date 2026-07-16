package com.medsy.medsy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MainState(
    val themeMode: ThemeMode? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
) : ViewModel() {
    val state = observeUserPreferencesUseCase()
        .map { preferences -> MainState(themeMode = preferences.themeMode) }
        .catch {
            emit(MainState(themeMode = ThemeMode.System))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainState(),
        )
}
