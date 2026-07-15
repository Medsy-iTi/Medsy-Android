package com.medsy.presentation.aichat

sealed interface AiChatUIEffect {
    data object NavigateNext : AiChatUIEffect
}
