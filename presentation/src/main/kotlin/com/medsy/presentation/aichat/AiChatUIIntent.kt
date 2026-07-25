package com.medsy.presentation.aichat

import com.medsy.domain.aichat.model.AiChatImageKind
import com.medsy.domain.aichat.model.AiChatScenario
import com.medsy.domain.aichat.model.AiPharmacy

sealed interface AiChatUIIntent {
    data class InputChanged(val value: String) : AiChatUIIntent
    data object SendClicked : AiChatUIIntent
    data class QuickActionClicked(val scenario: AiChatScenario) : AiChatUIIntent
    data object AttachmentClicked : AiChatUIIntent
    data object DismissAttachmentMenu : AiChatUIIntent
    data class CameraClicked(val kind: AiChatImageKind) : AiChatUIIntent
    data class GalleryClicked(val kind: AiChatImageKind) : AiChatUIIntent
    data class ImageSelected(
        val kind: AiChatImageKind,
        val uri: String?,
    ) : AiChatUIIntent

    data object VoiceClicked : AiChatUIIntent
    data class VoiceResult(val text: String?) : AiChatUIIntent
    data object NewChatClicked : AiChatUIIntent
    data object DismissNewChat : AiChatUIIntent
    data object ConfirmNewChat : AiChatUIIntent
    data class AddToCartClicked(val productId: Int) : AiChatUIIntent
    data class ReorderClicked(val productIds: List<Int>) : AiChatUIIntent
    data class CallPharmacyClicked(val phoneNumber: String) : AiChatUIIntent
    data class DirectionsClicked(val pharmacy: AiPharmacy) : AiChatUIIntent
    data object StartAiCallClicked : AiChatUIIntent
    data object EndAiCallClicked : AiChatUIIntent
    data object CallAmbulanceClicked : AiChatUIIntent
    data object FindNearestHospitalClicked : AiChatUIIntent
    data object SetReminderClicked : AiChatUIIntent
}
