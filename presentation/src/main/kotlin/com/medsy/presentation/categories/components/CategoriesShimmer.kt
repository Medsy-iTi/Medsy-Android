package com.medsy.presentation.categories.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun CategoriesShimmer(
    modifier: Modifier = Modifier
) {
    MedsyShimmer(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(2) {
                            MedsyShimmerPlaceholder(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(140.dp),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
