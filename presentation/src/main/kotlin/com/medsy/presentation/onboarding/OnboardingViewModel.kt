package com.medsy.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.medsy.presentation.onboarding.model.OnboardingPage
import com.medsy.presentation.R
import com.medsy.designsystem.R as DesignR

sealed interface OnboardingEvent {
    data object NavigateToLogin : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                _state.value = OnboardingState(
                    pages = listOf(
                        OnboardingPage(
                            imageRes = DesignR.drawable.img_onboarding_order,
                            titleRes = R.string.onboarding_title_1,
                            descriptionRes = R.string.onboarding_desc_1
                        ),
                        OnboardingPage(
                            imageRes = DesignR.drawable.img_onboarding_delivery,
                            titleRes = R.string.onboarding_title_2,
                            descriptionRes = R.string.onboarding_desc_2
                        ),
                        OnboardingPage(
                            imageRes = DesignR.drawable.img_onboarding_trusted,
                            titleRes = R.string.onboarding_title_3,
                            descriptionRes = R.string.onboarding_desc_3
                        )
                    )
                )
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OnboardingState()
        )
        
    private val _events = Channel<OnboardingEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.OnSkipClick,
            OnboardingAction.OnGetStartedClick -> {
                viewModelScope.launch {
                    _events.send(OnboardingEvent.NavigateToLogin)
                }
            }
        }
    }
}