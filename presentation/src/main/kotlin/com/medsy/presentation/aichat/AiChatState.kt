package com.medsy.presentation.aichat

import androidx.annotation.StringRes
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.prescription.model.PrescriptionImage

data class AiChatState(
    val messages: List<AiChatMessage> = emptyList(),
    val input: String = "",
    val isResponding: Boolean = false,
    val isLoadingHistory: Boolean = true,
    @StringRes val historyErrorRes: Int? = null,
    val pendingAttachment: PendingAttachment? = null,
    val isAttachSheetVisible: Boolean = false,
    val isNewChatDialogVisible: Boolean = false,
    val failedSubmission: AiChatOutgoingMessage? = null,
    @StringRes val errorMessageRes: Int? = null,
) {
    val canSend: Boolean
        get() = !isResponding && !isLoadingHistory && historyErrorRes == null
}

data class PendingAttachment(
    val image: PrescriptionImage,
)
