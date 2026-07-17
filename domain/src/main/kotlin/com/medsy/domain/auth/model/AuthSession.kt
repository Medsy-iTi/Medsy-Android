package com.medsy.domain.auth.model

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
