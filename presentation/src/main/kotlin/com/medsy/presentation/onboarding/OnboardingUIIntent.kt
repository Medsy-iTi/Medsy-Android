package com.medsy.presentation.onboarding

sealed interface OnboardingUIIntent {
    data object OnSkipClick : OnboardingUIIntent
    data object OnGetStartedClick : OnboardingUIIntent
}
