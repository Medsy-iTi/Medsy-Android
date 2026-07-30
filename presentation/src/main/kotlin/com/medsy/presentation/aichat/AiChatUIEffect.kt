package com.medsy.presentation.aichat

import androidx.annotation.StringRes

sealed interface AiChatUIEffect {
    data object LaunchVoiceInput : AiChatUIEffect
    data class OpenProduct(val productId: Int) : AiChatUIEffect
    data class ShowMessage(@StringRes val messageRes: Int) : AiChatUIEffect
}
