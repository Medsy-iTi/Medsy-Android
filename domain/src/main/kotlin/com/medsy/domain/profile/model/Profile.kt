package com.medsy.domain.profile.model

data class Profile(
    val name: String,
    val image: String?,
    val email: String,
    val phoneNumber: String,
    val age: Int,
)
