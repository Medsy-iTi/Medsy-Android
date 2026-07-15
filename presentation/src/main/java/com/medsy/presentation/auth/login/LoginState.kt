package com.medsy.presentation.auth.login

data class LoginState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)
