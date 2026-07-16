package com.medsy.presentation.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R
import com.medsy.presentation.auth.register.components.AuthTextField
import com.medsy.presentation.auth.register.components.ScreenHeader

@Composable
fun OtpRoot(
    email: String,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OtpEffect.NavigateHome -> onNavigateHome()
            }
        }
    }

    OtpScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun OtpScreen(
    state: OtpState,
    onIntent: (OtpIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeader(
                titleRes = R.string.auth_verify_otp,
                subtitleRes = R.string.auth_verify_otp,
                onBackClick = onNavigateBack,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = "Enter the 6-digit code sent to ${state.email}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                value = state.code,
                onValueChange = { if (it.length <= 6) onIntent(OtpIntent.CodeChanged(it)) },
                labelRes = R.string.auth_otp_code,
                leadingIcon = null,
                errorRes = null,
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(24.dp))

            MedsyButton(
                onClick = { onIntent(OtpIntent.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.code.length == 6,
                isLoading = state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.auth_verify_otp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.countdown > 0) {
                Text(
                    text = stringResource(R.string.auth_resend_cooldown, state.countdown),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                TextButton(onClick = { onIntent(OtpIntent.Resend) }) {
                    Text(
                        text = stringResource(R.string.auth_resend_otp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
