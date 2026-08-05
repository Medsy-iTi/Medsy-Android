package com.medsy.presentation.aichat

sealed interface AiChatUIIntent {
    data class InputChanged(val value: String) : AiChatUIIntent
    data object SendClicked : AiChatUIIntent
    data class QuickActionClicked(val question: String) : AiChatUIIntent
    data object RetrySend : AiChatUIIntent
    data object RetryHistory : AiChatUIIntent
    data object VoiceClicked : AiChatUIIntent
    data class VoiceResult(val text: String?) : AiChatUIIntent
    data object NewChatClicked : AiChatUIIntent
    data object DismissNewChat : AiChatUIIntent
    data object ConfirmNewChat : AiChatUIIntent
    data class ProductClicked(val productId: Int) : AiChatUIIntent
    data class AddToCartClicked(val productId: Int) : AiChatUIIntent

    data object AttachClicked : AiChatUIIntent
    data object AttachSheetDismissed : AiChatUIIntent
    data object CameraClicked : AiChatUIIntent
    data object GalleryClicked : AiChatUIIntent
    data class CameraCaptureCompleted(val success: Boolean) : AiChatUIIntent
    data class GalleryImageSelected(val uri: String?) : AiChatUIIntent
    data object RemoveAttachmentClicked : AiChatUIIntent

    data class EmergencyCallClicked(val number: String) : AiChatUIIntent
    data class CategoryClicked(val categoryId: Int, val categoryName: String) : AiChatUIIntent
    data object ViewCartClicked : AiChatUIIntent
    data object ConfirmRequestClicked : AiChatUIIntent
}
