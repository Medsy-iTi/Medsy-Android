package com.medsy.presentation.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.onboarding.OnboardingConstants
import com.medsy.presentation.onboarding.model.OnboardingPage
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
