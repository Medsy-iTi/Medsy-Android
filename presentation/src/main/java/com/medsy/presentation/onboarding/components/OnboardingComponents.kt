package com.medsy.presentation.onboarding.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.BorderGray
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.R
import com.medsy.presentation.onboarding.OnboardingConstants
import com.medsy.presentation.onboarding.OnboardingPage
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun OnboardingPageItem(
    pagerState: PagerState,
    page: Int,
    onboardingPage: OnboardingPage
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                alpha = (1f - (pageOffset * OnboardingConstants.FADE_ANIMATION_MULTIPLIER)).coerceIn(0f, 1f)
                translationY = (pageOffset * OnboardingConstants.TRANSLATE_Y_MULTIPLIER)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            painter = painterResource(id = onboardingPage.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(OnboardingConstants.SpacerLarge))

        Text(
            text = stringResource(id = onboardingPage.titleRes),
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = OnboardingConstants.ContentPaddingHorizontal)
        )

        Spacer(modifier = Modifier.height(OnboardingConstants.SpacerMedium))

        Text(
            text = stringResource(id = onboardingPage.descriptionRes),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = SecondaryText
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = OnboardingConstants.ContentPaddingHorizontal)
        )

        Spacer(modifier = Modifier.height(OnboardingConstants.SpacerMedium))
    }
}

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

@Composable
fun OnboardingNextButton(
    pagerState: PagerState,
    pageCount: Int,
    onGetStarted: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            if (pagerState.currentPage == pageCount - 1) {
                onGetStarted()
            } else {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = OnboardingConstants.ScreenPaddingHorizontal)
            .padding(bottom = OnboardingConstants.SpacerLarge)
            .height(OnboardingConstants.ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(OnboardingConstants.ButtonCornerRadius)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (pagerState.currentPage == pageCount - 1)
                    stringResource(R.string.action_get_started)
                else
                    stringResource(R.string.action_next),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeutralWhite
                )
            )
            Spacer(modifier = Modifier.width(OnboardingConstants.SpacerSmall))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = NeutralWhite
            )
        }
    }
}
