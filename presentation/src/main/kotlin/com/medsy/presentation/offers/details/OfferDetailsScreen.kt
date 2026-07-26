package com.medsy.presentation.offers.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.medsy.designsystem.ui.theme.extendedColors
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.R
import com.medsy.presentation.offers.OffersState
import com.medsy.presentation.offers.OffersUIEffect
import com.medsy.presentation.offers.OffersUIIntent
import com.medsy.presentation.offers.OffersViewModel
import com.medsy.presentation.offers.components.MedicineItemRow
import com.medsy.presentation.offers.components.OfferTopAppBar
import com.medsy.presentation.offers.components.PharmacistNote
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun OfferDetailsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToOrderReview: () -> Unit,
    viewModel: OffersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OffersUIEffect.NavigateBack -> onNavigateBack()
                is OffersUIEffect.NavigateToOrderReview -> onNavigateToOrderReview()
                else -> Unit
            }
        }
    }

    OfferDetailsScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun OfferDetailsScreen(
    state: OffersState,
    onIntent: (OffersUIIntent) -> Unit
) {
    val offer = state.selectedOffer
    
    Scaffold(
        topBar = {
            OfferTopAppBar(
                title = stringResource(R.string.offers_details_title),
                onBackClick = { onIntent(OffersUIIntent.NavigateBack) }
            )
        },
        bottomBar = {
            if (offer != null) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { onIntent(OffersUIIntent.ChooseSelectedOffer) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.extendedColors.badgeSuccess
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.offers_choose_offer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.extendedColors.onBadgeSuccess
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                MedsyShimmer(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MedsyShimmerPlaceholder(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(8.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                MedsyShimmerPlaceholder(modifier = Modifier.height(20.dp).fillMaxWidth(0.5f))
                                Spacer(modifier = Modifier.height(8.dp))
                                MedsyShimmerPlaceholder(modifier = Modifier.height(14.dp).fillMaxWidth(0.3f))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        MedsyShimmerPlaceholder(modifier = Modifier.height(24.dp).fillMaxWidth(0.4f))
                        Spacer(modifier = Modifier.height(16.dp))
                        repeat(3) {
                            MedsyShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else if (offer != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.extendedColors.badgeSuccess.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = MaterialTheme.extendedColors.badgeSuccess
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = stringResource(R.string.offers_pharmacy_title_format, offer.pharmacyName),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.offers_manager_format, offer.managerName),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    item {
                        Text(
                            text = stringResource(R.string.offers_requested_medicines),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    items(offer.medicines) { medicine ->
                        MedicineItemRow(medicine = medicine, isSingleLinePrice = false)
                    }

                    if (!offer.pharmacistComment.isNullOrEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            PharmacistNote(note = offer.pharmacistComment)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { /* View prescription logic */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.offers_view_prescription),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Offer not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
