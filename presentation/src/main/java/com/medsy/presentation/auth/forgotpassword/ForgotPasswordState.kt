package com.medsy.presentation.auth.forgotpassword

data class ForgotPasswordState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)
