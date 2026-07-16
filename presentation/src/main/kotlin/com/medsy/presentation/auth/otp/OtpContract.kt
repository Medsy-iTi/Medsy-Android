package com.medsy.presentation.auth.otp

data class OtpState(
    val email: String = "",
    val code: String = "",
    val countdown: Int = 300, // 5 minutes
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface OtpIntent {
    data class CodeChanged(val value: String) : OtpIntent
    data object Submit : OtpIntent
    data object Resend : OtpIntent
    data object Tick : OtpIntent
}

sealed interface OtpEffect {
    data object NavigateHome : OtpEffect
}
