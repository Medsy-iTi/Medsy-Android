package com.medsy.presentation.onboarding.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.medsy.designsystem.ui.theme.BorderGray
import com.medsy.presentation.onboarding.OnboardingConstants

@Composable
fun OnboardingIndicator(
    pagerState: PagerState,
    pageCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OnboardingConstants.SpacerLarge),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            val width by animateDpAsState(
                targetValue = if (isSelected) OnboardingConstants.IndicatorSelectedWidth else OnboardingConstants.IndicatorUnselectedWidth,
                animationSpec = tween(durationMillis = OnboardingConstants.INDICATOR_ANIMATION_DURATION),
                label = OnboardingConstants.INDICATOR_ANIMATION_LABEL
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = OnboardingConstants.IndicatorSpacing)
                    .height(OnboardingConstants.IndicatorHeight)
                    .width(width)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else BorderGray
                    )
            )
        }
    }
}
