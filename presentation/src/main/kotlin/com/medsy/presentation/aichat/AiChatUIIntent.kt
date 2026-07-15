package com.medsy.presentation.aichat

sealed interface AiChatUIIntent {
    data object OnNextClick : AiChatUIIntent
}
