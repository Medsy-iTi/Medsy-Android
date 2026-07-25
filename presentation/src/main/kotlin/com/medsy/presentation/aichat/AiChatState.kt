package com.medsy.presentation.aichat

import com.medsy.domain.aichat.model.AiChatImageKind
import com.medsy.domain.aichat.model.AiChatMessage

data class AiChatState(
    val messages: List<AiChatMessage> = emptyList(),
    val input: String = "",
    val isResponding: Boolean = false,
    val isAttachmentMenuVisible: Boolean = false,
    val isNewChatDialogVisible: Boolean = false,
    val pendingImageKind: AiChatImageKind? = null,
    val isAiCallInProgress: Boolean = false,
    val isReminderConfirmed: Boolean = false,
)
