package com.medsy.presentation.pharmacyprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.domain.pharmacyprofile.model.PharmacyProfile
import com.medsy.presentation.pharmacyprofile.components.PharmacyLocationCard
import com.medsy.presentation.pharmacyprofile.components.PharmacyPhoneCard
import com.medsy.presentation.pharmacyprofile.components.PharmacyProfileActions
import com.medsy.presentation.pharmacyprofile.components.PharmacyProfileError
import com.medsy.presentation.pharmacyprofile.components.PharmacyProfileHeader
import com.medsy.presentation.pharmacyprofile.components.PharmacyProfileLoading

@Composable
fun PharmacyProfileRoot(
    pharmacyId: Long = 6L,
    onNavigateBack: () -> Unit,
    onDialPhone: (String) -> Unit,
    onOpenDirections: (Double, Double) -> Unit,
    viewModel: PharmacyProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pharmacyId) {
        viewModel.init(pharmacyId)
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PharmacyProfileUIEffect.NavigateBack -> onNavigateBack()
                is PharmacyProfileUIEffect.DialPhone -> onDialPhone(effect.phoneNumber)
                is PharmacyProfileUIEffect.OpenDirections -> onOpenDirections(
                    effect.latitude,
                    effect.longitude,
                )
            }
        }
    }

    PharmacyProfileScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun PharmacyProfileScreen(
    state: PharmacyProfileState,
    onIntent: (PharmacyProfileUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        when {
            state.isLoading -> PharmacyProfileStatusLayout(
                pharmacyName = null,
                onBackClick = { onIntent(PharmacyProfileUIIntent.BackClicked) },
                modifier = Modifier.padding(paddingValues),
            ) { statusModifier ->
                PharmacyProfileLoading(modifier = statusModifier)
            }

            state.errorMessageRes != null -> PharmacyProfileStatusLayout(
                pharmacyName = null,
                onBackClick = { onIntent(PharmacyProfileUIIntent.BackClicked) },
                modifier = Modifier.padding(paddingValues),
            ) { statusModifier ->
                PharmacyProfileError(
                    messageRes = state.errorMessageRes,
                    onRetryClick = { onIntent(PharmacyProfileUIIntent.RetryClicked) },
                    modifier = statusModifier,
                )
            }

            state.pharmacy != null -> PharmacyProfileContent(
                pharmacy = state.pharmacy,
                onBackClick = { onIntent(PharmacyProfileUIIntent.BackClicked) },
                onCallClick = { onIntent(PharmacyProfileUIIntent.CallClicked) },
                onDirectionsClick = {
                    onIntent(PharmacyProfileUIIntent.DirectionsClicked)
                },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun PharmacyProfileStatusLayout(
    pharmacyName: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        PharmacyProfileHeader(
            pharmacyName = pharmacyName,
            onBackClick = onBackClick,
        )
        content(Modifier.weight(1f))
    }
}

@Composable
private fun PharmacyProfileContent(
    pharmacy: PharmacyProfile,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canCall = !pharmacy.phoneNumber.isNullOrBlank()
    val canOpenDirections = pharmacy.latitude != null && pharmacy.longitude != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        PharmacyProfileHeader(
            pharmacyName = pharmacy.name,
            onBackClick = onBackClick,
        )
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 18.dp,
                end = 16.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            PharmacyProfileActions(
                canCall = canCall,
                canOpenDirections = canOpenDirections,
                onCallClick = onCallClick,
                onDirectionsClick = onDirectionsClick,
            )
            PharmacyLocationCard(
                pharmacyName = pharmacy.name,
                address = pharmacy.address,
                latitude = pharmacy.latitude,
                longitude = pharmacy.longitude,
                onDirectionsClick = onDirectionsClick,
            )
            PharmacyPhoneCard(
                phoneNumber = pharmacy.phoneNumber,
                onCallClick = onCallClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PharmacyProfilePreview() {
    MedsyTheme {
        PharmacyProfileScreen(
            state = PharmacyProfileState(
                isLoading = false,
                pharmacy = PharmacyProfile(
                    id = 6L,
                    name = "Al Hassan Pharmacy",
                    latitude = 26.155727476817233,
                    longitude = 32.716335989534855,
                    address = "Governorate Street, Qena",
                    phoneNumber = "01157084789",
                ),
            ),
            onIntent = {},
        )
    }
}
