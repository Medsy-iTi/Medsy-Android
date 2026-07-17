package com.medsy.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.luminance
import com.medsy.designsystem.R as DesignR

import com.medsy.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
                SplashEffect.ToOnboarding -> openOnboarding()
                SplashEffect.ToLogin -> openLogin()
            }
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {

    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(SplashConstants.TEXT_SLIDE_START_OFFSET) }

    LaunchedEffect(Unit) {

        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.LOGO_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            )
        }

        delay(SplashConstants.LOGO_ANIMATION_DELAY.milliseconds)

        val textAlphaJob = launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.TEXT_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        }

        val textOffsetJob = launch {
            textOffsetY.animateTo(
                targetValue = SplashConstants.TEXT_SLIDE_END_OFFSET,
                animationSpec = tween(
                    durationMillis = SplashConstants.TEXT_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        }

        textAlphaJob.join()
        textOffsetJob.join()

        delay(SplashConstants.HOLD_DURATION.milliseconds)
    }

    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val logoRes = DesignR.drawable.ic_logo_transparent

            Image(
                painter = painterResource(id = logoRes),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer { alpha = logoAlpha.value },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            val primaryColor = MaterialTheme.colorScheme.primary
            val textColor = MaterialTheme.colorScheme.onBackground

            val prefix = stringResource(R.string.app_name_prefix)
            val suffix = stringResource(R.string.app_name_suffix)

            Text(
                modifier = Modifier
                    .graphicsLayer { alpha = textAlpha.value }
                    .offset { IntOffset(0, textOffsetY.value.toInt()) },

                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = primaryColor)) {
                        append(prefix)
                    }
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(suffix)
                    }
                },

                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}