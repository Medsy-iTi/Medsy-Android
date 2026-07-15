package com.medsy.presentation.aichat

sealed interface AiChatEvent {
    data object NavigateNext : AiChatEvent
}
