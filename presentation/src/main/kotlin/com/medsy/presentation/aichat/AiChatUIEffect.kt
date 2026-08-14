package com.medsy.presentation.aichat

import androidx.annotation.StringRes

sealed interface AiChatUIEffect {
    data object LaunchVoiceInput : AiChatUIEffect
    data class LaunchCamera(val uri: String) : AiChatUIEffect
    data object LaunchGallery : AiChatUIEffect
    data class OpenProduct(val productId: Int) : AiChatUIEffect
    data class OpenCategory(val categoryId: Int, val categoryName: String) : AiChatUIEffect
    data class DialNumber(val number: String) : AiChatUIEffect
    data object NavigateToCartTab : AiChatUIEffect
    data object NavigateToCartRequest : AiChatUIEffect
    data class RequestNotificationPermission(
        val reminderId: Long,
        val messageId: Long,
    ) : AiChatUIEffect
    data object OpenExactAlarmSettings : AiChatUIEffect
    data object OpenBatteryOptimizationSettings : AiChatUIEffect
    data class ShowMessage(
        @StringRes val messageRes: Int,
        val isSuccess: Boolean = false
    ) : AiChatUIEffect
}
