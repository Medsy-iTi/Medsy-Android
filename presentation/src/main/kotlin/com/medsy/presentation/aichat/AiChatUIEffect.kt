package com.medsy.presentation.aichat

import androidx.annotation.StringRes
import com.medsy.domain.aichat.model.AiChatImageKind

sealed interface AiChatUIEffect {
    data class LaunchCamera(
        val kind: AiChatImageKind,
        val uri: String,
    ) : AiChatUIEffect

    data class LaunchGallery(val kind: AiChatImageKind) : AiChatUIEffect
    data object LaunchVoiceInput : AiChatUIEffect
    data class DialPhone(val phoneNumber: String) : AiChatUIEffect
    data class OpenDirections(
        val latitude: Double,
        val longitude: Double,
        val label: String,
    ) : AiChatUIEffect
    data class OpenMapSearch(@StringRes val queryRes: Int) : AiChatUIEffect

    data class ShowMessage(@StringRes val messageRes: Int) : AiChatUIEffect
}
