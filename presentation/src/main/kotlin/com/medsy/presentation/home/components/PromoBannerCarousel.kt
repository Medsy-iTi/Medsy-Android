package com.medsy.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.presentation.home.PromoBannerUi

@Composable
fun PromoBannerCarousel(
    banners: List<PromoBannerUi>, currentIndex: Int, onPromoClick: () -> Unit
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    if (banners.isNotEmpty()) {
        val pagerState = rememberPagerState(pageCount = { banners.size })

        HorizontalPager(state = pagerState) { page ->
            val currentBanner = banners[page]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(210.dp)
                    .shadow(
                        elevation = if (isDark) 16.dp else 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onPromoClick() }) {
                Image(
                    painter = painterResource(id = currentBanner.imageRes),
                    contentDescription = stringResource(currentBanner.imageContentDescRes),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.65f), Color.Black.copy(alpha = 0.15f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.65f)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(currentBanner.extraTitleRes),
                        color = Color.White.copy(alpha = 0.80f),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(currentBanner.titleRes),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(currentBanner.subtitleRes),
                        color = Color.White.copy(alpha = 0.90f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (banners.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                banners.forEachIndexed { index, _ ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .width(if (isSelected) 18.dp else 8.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.LightGray
                            )
                    )
                    if (index < banners.lastIndex) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }
    }
}
