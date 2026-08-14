package com.medsy.medsy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.reminders.usecase.CancelScheduledMedicationRemindersUseCase
import com.medsy.domain.reminders.usecase.RescheduleMedicationRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainState(
    val themeMode: ThemeMode? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    observeSessionUseCase: ObserveSessionUseCase,
    private val rescheduleReminders: RescheduleMedicationRemindersUseCase,
    private val cancelScheduledReminders: CancelScheduledMedicationRemindersUseCase,
) : ViewModel() {
    private var scheduledUserId = 0L

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

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                val userId = session?.user?.id ?: 0L
                if (scheduledUserId > 0L && scheduledUserId != userId) {
                    cancelScheduledReminders(scheduledUserId)
                }
                scheduledUserId = userId
                if (userId > 0L) {
                    rescheduleReminders(userId)
                }
            }
        }
    }
}
