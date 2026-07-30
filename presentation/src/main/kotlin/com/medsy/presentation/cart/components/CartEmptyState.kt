package com.medsy.presentation.cart.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import androidx.compose.material.icons.filled.Search
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R

private val capsuleMotionEasing = CubicBezierEasing(
    a = 0.333f,
    b = 0f,
    c = 0.667f,
    d = 1f,
)

@Composable
internal fun CartEmptyState(
    onSearchMedicineClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.cart_empty_medicine)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1.15f,
    )
    val capsulePhase = if (progress <= 0.5f) {
        progress * 2f
    } else {
        (1f - progress) * 2f
    }
    val capsuleEasedProgress = capsuleMotionEasing.transform(capsulePhase)
    val capsuleTopOffset = (50f - (11f * capsuleEasedProgress)).dp
    val capsuleRotation = -10f + (18f * capsuleEasedProgress)
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.primary.toArgb(),
            "**",
            "Primary Fill",
        ),
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.primary.toArgb(),
            "**",
            "Primary Stroke",
        ),
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.primaryContainer.toArgb(),
            "**",
            "Container Fill",
        ),
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.tertiary.toArgb(),
            "**",
            "Accent Fill",
        ),
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.surface.toArgb(),
            "**",
            "Surface Fill",
        ),
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.onSurfaceVariant.toArgb(),
            "**",
            "Outline Stroke",
        ),
        rememberLottieDynamicProperty(
            LottieProperty.COLOR,
            colors.onSurfaceVariant.toArgb(),
            "**",
            "Shadow Fill",
        ),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clearAndSetSemantics {},
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                dynamicProperties = dynamicProperties,
                modifier = Modifier.fillMaxSize(),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .absoluteOffset(x = 46.dp, y = (-38).dp)
                    .size(22.dp),
            )
            Box(
                modifier = Modifier
                    .absoluteOffset(x = 28.dp, y = capsuleTopOffset)
                    .size(width = 75.dp, height = 26.dp)
                    .graphicsLayer {
                        rotationZ = capsuleRotation
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.cart_empty_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = colors.onPrimary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .absoluteOffset(x = 4.dp, y = 26.dp)
                    .size(22.dp),
            )
        }
        Text(
            text = stringResource(R.string.cart_empty),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.cart_empty_supporting),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        MedsyButton(
            onClick = onSearchMedicineClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.home_card_search_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
