package com.medsy.presentation.products.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun ProductsShimmer(
    modifier: Modifier = Modifier
) {
    MedsyShimmer(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .width(120.dp)
                        .height(24.dp)
                )
            }

            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                repeat(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(2) {
                            MedsyShimmerPlaceholder(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(280.dp),
                                shape = RoundedCornerShape(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
