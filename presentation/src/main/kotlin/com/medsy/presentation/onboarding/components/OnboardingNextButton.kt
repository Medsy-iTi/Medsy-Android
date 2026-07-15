package com.medsy.presentation.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.presentation.R
import com.medsy.presentation.onboarding.OnboardingConstants
import kotlinx.coroutines.launch

@Composable
fun OnboardingNextButton(
    pagerState: PagerState,
    pageCount: Int,
    onGetStarted: () -> Unit
) {
    val scope = rememberCoroutineScope()

    MedsyButton(
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
            .padding(horizontal = OnboardingConstants.ScreenPaddingHorizontal)
            .padding(bottom = OnboardingConstants.SpacerLarge)
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
