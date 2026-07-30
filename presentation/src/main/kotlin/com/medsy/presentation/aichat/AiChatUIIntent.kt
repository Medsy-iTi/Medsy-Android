package com.medsy.presentation.aichat

sealed interface AiChatUIIntent {
    data class InputChanged(val value: String) : AiChatUIIntent
    data object SendClicked : AiChatUIIntent
    data class QuickActionClicked(val question: String) : AiChatUIIntent
    data object RetryCatalogQuestion : AiChatUIIntent
    data object VoiceClicked : AiChatUIIntent
    data class VoiceResult(val text: String?) : AiChatUIIntent
    data object NewChatClicked : AiChatUIIntent
    data object DismissNewChat : AiChatUIIntent
    data object ConfirmNewChat : AiChatUIIntent
    data class ProductClicked(val productId: Int) : AiChatUIIntent
    data class AddToCartClicked(val productId: Int) : AiChatUIIntent
}
