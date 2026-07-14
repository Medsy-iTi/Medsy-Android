package com.medsy.presentation.onboarding

data class OnboardingState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)