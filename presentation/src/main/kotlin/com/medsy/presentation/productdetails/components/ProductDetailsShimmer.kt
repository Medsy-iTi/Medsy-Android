package com.medsy.presentation.productdetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun ProductDetailsShimmer(
    modifier: Modifier = Modifier
) {
    MedsyShimmer(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                )
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) {
                        MedsyShimmerPlaceholder(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp),
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(200.dp)
                            .height(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(100.dp)
                            .height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(80.dp)
                            .height(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .width(150.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    repeat(3) {
                        MedsyShimmerPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MedsyShimmerPlaceholder(modifier = Modifier
                                .width(100.dp)
                                .height(16.dp))
                            MedsyShimmerPlaceholder(modifier = Modifier
                                .width(80.dp)
                                .height(16.dp))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
