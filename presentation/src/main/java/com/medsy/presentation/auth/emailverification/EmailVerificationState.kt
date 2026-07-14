package com.medsy.presentation.auth.emailverification

data class EmailVerificationState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)
