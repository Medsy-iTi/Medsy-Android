package com.medsy.presentation.pharmacyprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.domain.pharmacyprofile.model.PharmacyProfile
import com.medsy.presentation.R
import com.medsy.presentation.pharmacyprofile.components.PharmacyContactCard
import com.medsy.presentation.pharmacyprofile.components.PharmacyLocationCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyProfileScreen(
    state: PharmacyProfileState,
    onIntent: (PharmacyProfileUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pharmacy_profile_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(PharmacyProfileUIIntent.BackClicked) },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            state.isLoading -> PharmacyProfileLoading(
                modifier = Modifier.padding(paddingValues),
            )

            state.errorMessageRes != null -> PharmacyProfileError(
                messageRes = state.errorMessageRes,
                onRetryClick = { onIntent(PharmacyProfileUIIntent.RetryClicked) },
                modifier = Modifier.padding(paddingValues),
            )

            state.pharmacy != null -> PharmacyProfileContent(
                pharmacy = state.pharmacy,
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
private fun PharmacyProfileContent(
    pharmacy: PharmacyProfile,
    onCallClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PharmacyProfileHeader(pharmacyName = pharmacy.name)
        PharmacyContactCard(
            phoneNumber = pharmacy.phoneNumber,
            address = pharmacy.address,
            onCallClick = onCallClick,
        )
        PharmacyLocationCard(
            pharmacyName = pharmacy.name,
            latitude = pharmacy.latitude,
            longitude = pharmacy.longitude,
            onDirectionsClick = onDirectionsClick,
        )
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
                    name = "الحسن والحسين",
                    latitude = 26.155727476817233,
                    longitude = 32.716335989534855,
                    address = "قنا - شارع المحافظه",
                    phoneNumber = "01157084789",
                ),
            ),
            onIntent = {},
        )
    }
}
