package com.medsy.presentation.offers.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.R
import com.medsy.presentation.offers.OffersState
import com.medsy.presentation.offers.OffersUIEffect
import com.medsy.presentation.offers.OffersUIIntent
import com.medsy.presentation.offers.OffersViewModel
import com.medsy.presentation.offers.components.MedicineItemRow
import com.medsy.presentation.offers.components.OfferTopAppBar
import com.medsy.presentation.offers.components.PharmacistNote

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
                title = stringResource(R.string.offers_pharmacy_title_format, offer?.pharmacyName.orEmpty()),
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
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.offers_choose_offer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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
            if (offer != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.offers_manager_format, offer.managerName),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    
                    item {
                        Text(
                            text = stringResource(R.string.offers_requested_medicines),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    items(offer.medicines) { medicine ->
                        MedicineItemRow(medicine = medicine)
                    }

                    if (!offer.pharmacistComment.isNullOrEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            PharmacistNote(note = offer.pharmacistComment)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { /* View prescription logic */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
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
