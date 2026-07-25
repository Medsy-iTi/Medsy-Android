package com.medsy.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.presentation.R
import com.medsy.presentation.splash.components.SplashWaves
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import com.medsy.designsystem.R as DesignR

@Composable
fun SplashRoot(
    openOnboarding: () -> Unit,
    openHome: () -> Unit,
    openLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.ToHome -> openHome()
                SplashEffect.ToLogin -> openLogin()
                SplashEffect.ToOnboarding -> openOnboarding()
            }
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {
    val isDark = isSystemInDarkTheme()

    val logoScale = remember { Animatable(0.55f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textTranslationY = remember { Animatable(SplashConstants.TEXT_SLIDE_START_OFFSET) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.LOGO_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.LOGO_ANIMATION_DURATION
                )
            )
        }

        delay(SplashConstants.LOGO_ANIMATION_DELAY.milliseconds)

        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.TEXT_ANIMATION_DURATION,
                )
            )
        }
        launch {
            textTranslationY.animateTo(
                targetValue = SplashConstants.TEXT_SLIDE_END_OFFSET,
                animationSpec = tween(
                    durationMillis = SplashConstants.TEXT_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                ),
            )
        }

        delay(SplashConstants.HOLD_DURATION.milliseconds)
    }

    val backgroundBrush = if (isDark) {
        Brush.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.background,
            ),
            center = Offset(0.5f, 0.35f),
            radius = 1200f,
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surface,
            ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center,
    ) {
        SplashWaves(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(260.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MedsyShimmer(
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
            ) {
                Image(
                    painter = painterResource(id = DesignR.drawable.ic_logo_transparent),
                    contentDescription = stringResource(R.string.splash_logo_description),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer(translationY = textTranslationY.value)
                    .alpha(textAlpha.value)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val textColor =
                    if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = primaryColor)) {
                            append(stringResource(R.string.app_name_prefix))
                        }
                        withStyle(style = SpanStyle(color = textColor)) {
                            append(stringResource(R.string.app_name_suffix))
                        }
                    },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 0.3.sp,
                    ),
                    color = if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.7f
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}
