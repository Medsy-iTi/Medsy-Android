package com.medsy.presentation.home.components.activesearch

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

@Composable
fun ActiveSearchStages(currentStage: Int) {
    val stages = listOf(
        R.string.home_search_status_stage_1 to R.string.home_search_status_stage_1_desc,
        R.string.home_search_status_stage_2 to R.string.home_search_status_stage_2_desc,
        R.string.home_search_status_stage_3 to R.string.home_search_status_stage_3_desc,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        stages.forEachIndexed { index, stage ->
            val stageNumber = index + 1
            val reached = currentStage >= stageNumber
            val current = currentStage == stageNumber
            val color by animateColorAsState(
                if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                animationSpec = tween(400),
                label = "search-stage-color",
            )
            val circleSize by animateDpAsState(
                if (current) 20.dp else 16.dp,
                animationSpec = tween(400),
                label = "search-stage-size",
            )
            val innerSize by animateDpAsState(
                if (reached) 10.dp else 0.dp,
                animationSpec = tween(400),
                label = "search-stage-inner-size",
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
            ) {
                Text(
                    stringResource(stage.first),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    stringResource(stage.second),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (current) color else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    minLines = 2,
                    maxLines = 2,
                )
                Spacer(Modifier.height(12.dp))
                Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                    if (current) {
                        val transition = rememberInfiniteTransition(label = "search-stage-ring")
                        val ringScale by transition.animateFloat(
                            1f,
                            1.6f,
                            infiniteRepeatable(
                                tween(1_000, easing = LinearOutSlowInEasing),
                                RepeatMode.Restart
                            ),
                            label = "search-stage-ring-scale",
                        )
                        val ringAlpha by transition.animateFloat(
                            0.5f,
                            0f,
                            infiniteRepeatable(
                                tween(1_000, easing = LinearOutSlowInEasing),
                                RepeatMode.Restart
                            ),
                            label = "search-stage-ring-alpha",
                        )
                        Box(
                            Modifier
                                .size(circleSize)
                                .scale(ringScale)
                                .border(2.dp, color.copy(alpha = ringAlpha), CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .border(2.dp, color, CircleShape)
                            .background(MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(Modifier
                            .size(innerSize)
                            .clip(CircleShape)
                            .background(color))
                    }
                }
            }

            if (index < stages.lastIndex) {
                val lineColor by animateColorAsState(
                    if (currentStage > stageNumber) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    animationSpec = tween(400),
                    label = "search-stage-line",
                )
                Box(Modifier
                    .weight(0.5f)
                    .padding(top = 54.dp)
                    .height(2.dp)
                    .background(lineColor))
            }
        }
    }
}
