package com.medsy.presentation.aichat

sealed interface AiChatAction {
    data object OnNextClick : AiChatAction
}
