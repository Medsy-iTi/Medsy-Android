package com.medsy.presentation.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ForgotPasswordState()
        )

    private val _events = Channel<ForgotPasswordEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            ForgotPasswordAction.OnNextClick -> sendEvent(ForgotPasswordEvent.NavigateNext)
        }
    }

    private fun sendEvent(event: ForgotPasswordEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }
}
