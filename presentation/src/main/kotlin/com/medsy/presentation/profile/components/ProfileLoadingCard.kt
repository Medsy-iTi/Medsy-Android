package com.medsy.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder
import com.medsy.presentation.R

@Composable
fun ProfileLoadingCard(
    modifier: Modifier = Modifier,
) {
    val loadingDescription = stringResource(R.string.profile_loading)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp)
            .clearAndSetSemantics {
                contentDescription = loadingDescription
            },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        MedsyShimmer(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 24.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(76.dp),
                    shape = CircleShape,
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(20.dp),
                        shape = RoundedCornerShape(10.dp),
                    )

                    MedsyShimmerPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .height(15.dp),
                    )
                }
            }
        }
    }
}
