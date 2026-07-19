package com.medsy.presentation.home.components.activesearch

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

@Composable
fun ActiveSearchStages(currentStage: Int) {
    val stages = listOf(
        Pair(R.string.home_search_status_stage_1, R.string.home_search_status_stage_1_desc),
        Pair(R.string.home_search_status_stage_2, R.string.home_search_status_stage_2_desc),
        Pair(R.string.home_search_status_stage_3, R.string.home_search_status_stage_3_desc)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        stages.forEachIndexed { index, pair ->
            val stageNum = index + 1
            val isCurrentOrPast = currentStage >= stageNum
            val isCurrent = currentStage == stageNum

            val targetColor = if (isCurrentOrPast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            val animatedColor by animateColorAsState(targetValue = targetColor, animationSpec = tween(400), label = "color")
            
            val circleSize by animateDpAsState(targetValue = if (isCurrent) 20.dp else 16.dp, animationSpec = tween(400), label = "size")
            val innerCircleSize by animateDpAsState(targetValue = if (isCurrentOrPast) 10.dp else 0.dp, animationSpec = tween(400), label = "innerSize")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text(
                    text = stringResource(pair.first),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = animatedColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(pair.second),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) animatedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    minLines = 2,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp), // Fixed box so expanding circle doesn't shift layout
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .border(
                                width = 2.dp,
                                color = animatedColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(innerCircleSize)
                                .clip(CircleShape)
                                .background(animatedColor)
                        )
                    }
                }
            }
            
            if (index < stages.size - 1) {
                val lineColor = if (currentStage > stageNum) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                val animatedLineColor by animateColorAsState(targetValue = lineColor, animationSpec = tween(400), label = "lineColor")
                
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(top = 48.dp) // Aligned with the center of the 24.dp box
                        .height(2.dp)
                        .background(animatedLineColor)
                )
            }
        }
    }
}
