package com.medsy.presentation.onboarding

sealed interface OnboardingAction {
    data object OnSkipClick : OnboardingAction
    data object OnGetStartedClick : OnboardingAction
}