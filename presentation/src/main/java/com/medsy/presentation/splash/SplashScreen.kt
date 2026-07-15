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
import com.medsy.designsystem.R as DesignR
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.designsystem.ui.theme.PrimaryText
import com.medsy.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashRoot(
    openOnboarding: () -> Unit,
) {
    SplashScreen(
        onFinished = openOnboarding
    )
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {

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

        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralWhite),
        contentAlignment = Alignment.Center,
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = DesignR.drawable.ic_logo_transparent),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer { alpha = logoAlpha.value },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            val primaryColor = MaterialTheme.colorScheme.primary
            val textColor = PrimaryText

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