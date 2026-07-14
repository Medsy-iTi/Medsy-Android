package com.medsy.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medsy.presentation.home.PromoBannerUi

@Composable
fun PromoBannerCarousel(
    banners: List<PromoBannerUi>,
    currentIndex: Int,
    onPromoClick: () -> Unit
) {
    val primaryGreen = Color(0xFF1E7B4D)

    if (banners.isNotEmpty()) {
        val currentBanner = banners[currentIndex]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryGreen, RoundedCornerShape(16.dp))
                .clickable { onPromoClick() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.5f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(currentBanner.titleRes),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(currentBanner.subtitleRes),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(currentBanner.discountTextRes),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onPromoClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = stringResource(currentBanner.ctaTextRes),
                            color = primaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (banners.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                banners.forEachIndexed { index, _ ->
                    val dotColor = if (index == currentIndex) primaryGreen else Color.LightGray
                    Box(modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, CircleShape))
                    if (index < banners.lastIndex) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}
