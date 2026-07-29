package com.medsy.presentation.orders.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun OrderDetailsShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MedsyShimmer(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 1. Status Stepper Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                MedsyShimmerPlaceholder(modifier = Modifier.width(120.dp).height(20.dp))
                                MedsyShimmerPlaceholder(modifier = Modifier.width(80.dp).height(12.dp))
                            }
                            MedsyShimmerPlaceholder(modifier = Modifier.width(70.dp).height(24.dp), shape = RoundedCornerShape(20.dp))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(4) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    MedsyShimmerPlaceholder(modifier = Modifier.size(24.dp), shape = RoundedCornerShape(12.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    MedsyShimmerPlaceholder(modifier = Modifier.width(50.dp).height(10.dp))
                                }
                            }
                        }
                    }
                }

                // 2. Pharmacy Card Shimmer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MedsyShimmerPlaceholder(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                MedsyShimmerPlaceholder(modifier = Modifier.width(80.dp).height(12.dp))
                                MedsyShimmerPlaceholder(modifier = Modifier.width(140.dp).height(16.dp))
                            }
                        }
                        MedsyShimmerPlaceholder(modifier = Modifier.size(20.dp))
                    }
                }

                // 3. Items Section Title
                MedsyShimmerPlaceholder(modifier = Modifier.width(100.dp).height(20.dp))

                // 4. Items List Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(2) { index ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MedsyShimmerPlaceholder(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(12.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    MedsyShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp))
                                    MedsyShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.3f).height(12.dp))
                                }
                                MedsyShimmerPlaceholder(modifier = Modifier.width(60.dp).height(16.dp))
                            }
                            if (index == 0) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                            }
                        }
                    }
                }

                // 5. Price Summary Shimmer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MedsyShimmerPlaceholder(modifier = Modifier.width(80.dp).height(18.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MedsyShimmerPlaceholder(modifier = Modifier.width(80.dp).height(14.dp))
                            MedsyShimmerPlaceholder(modifier = Modifier.width(60.dp).height(14.dp))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MedsyShimmerPlaceholder(modifier = Modifier.width(80.dp).height(14.dp))
                            MedsyShimmerPlaceholder(modifier = Modifier.width(60.dp).height(14.dp))
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MedsyShimmerPlaceholder(modifier = Modifier.width(100.dp).height(18.dp))
                            MedsyShimmerPlaceholder(modifier = Modifier.width(80.dp).height(18.dp))
                        }
                    }
                }
            }
        }
    }
}
