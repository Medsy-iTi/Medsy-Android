package com.medsy.presentation.aichat

import androidx.annotation.StringRes
import com.medsy.domain.aichat.model.AiChatMessage

data class AiChatState(
    val messages: List<AiChatMessage> = emptyList(),
    val input: String = "",
    val isResponding: Boolean = false,
    val isNewChatDialogVisible: Boolean = false,
    val failedQuestion: String? = null,
    @StringRes val errorMessageRes: Int? = null,
)
