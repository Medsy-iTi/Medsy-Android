package com.medsy.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.R
import com.medsy.presentation.onboarding.components.OnboardingIndicator
import com.medsy.presentation.onboarding.components.OnboardingNextButton
import com.medsy.presentation.onboarding.components.OnboardingPageItem

@Composable
fun OnboardingRoot(
    openLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingUIEffect.NavigateToLogin -> openLogin()
            }
        }
    }

    OnboardingScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onIntent: (OnboardingUIIntent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { state.pages.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralWhite)
    ) {
        // Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = OnboardingConstants.ScreenPaddingHorizontal,
                    vertical = OnboardingConstants.ScreenPaddingVertical
                ),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.action_skip),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = SecondaryText
                ),
                modifier = Modifier.clickable { onIntent(OnboardingUIIntent.OnSkipClick) }
            )
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageItem(
                pagerState = pagerState,
                page = page,
                onboardingPage = state.pages[page]
            )
        }

        // Indicators
        OnboardingIndicator(
            pagerState = pagerState,
            pageCount = state.pages.size
        )

        // Next Button
        OnboardingNextButton(
            pagerState = pagerState,
            pageCount = state.pages.size,
            onGetStarted = { onIntent(OnboardingUIIntent.OnGetStartedClick) }
        )
    }
}
