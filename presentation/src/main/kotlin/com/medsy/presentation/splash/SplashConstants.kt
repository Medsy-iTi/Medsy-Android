package com.medsy.presentation.splash

object SplashConstants {
    const val LOGO_ANIMATION_DURATION = 900
    const val LOGO_ANIMATION_DELAY = 300L

    const val TEXT_ANIMATION_DURATION = 700
    const val TEXT_SLIDE_START_OFFSET = 40f
    const val TEXT_SLIDE_END_OFFSET = 0f

    const val HOLD_DURATION = 800L

    // Total time before navigation = LOGO_ANIMATION_DELAY + TEXT_ANIMATION_DURATION + HOLD_DURATION
    // ≈ 300 + 700 + 800 = 1800ms
    const val MIN_SPLASH_DURATION_MS = 2200L
}
