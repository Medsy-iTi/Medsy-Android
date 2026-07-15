package com.medsy.presentation.onboarding

import com.medsy.presentation.onboarding.model.OnboardingPage

data class OnboardingState(
    val pages: List<OnboardingPage> = emptyList()
)