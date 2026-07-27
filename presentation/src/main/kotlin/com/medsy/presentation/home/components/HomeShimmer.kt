package com.medsy.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun HomeShimmer(
    modifier: Modifier = Modifier
) {
    MedsyShimmer(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(150.dp)
                            .height(18.dp)
                    )
                }
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .width(120.dp)
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(2) {
                        MedsyShimmerPlaceholder(
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(100.dp)
                            .height(20.dp)
                    )
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(60.dp)
                            .height(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                repeat(2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MedsyShimmerPlaceholder(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.1f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                MedsyShimmerPlaceholder(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
