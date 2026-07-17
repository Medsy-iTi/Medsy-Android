package com.medsy.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.feature.createaccount.components.SignInFooter
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
import com.medsy.presentation.R
import com.medsy.presentation.auth.register.components.AuthTextField
import com.medsy.presentation.auth.register.components.PasswordField
import com.medsy.presentation.auth.register.components.ScreenHeader
import com.medsy.presentation.auth.register.components.SectionTitle

@Composable
fun RegisterRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.NavigateToOtp -> onNavigateToOtp(effect.email)
                is RegisterEffect.ShowError     -> snackbarHostState.showError(
                    message = context.getString(effect.messageRes)
                )
            }
        }
    }

    RegisterScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToSignIn = onNavigateToSignIn,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onIntent: (RegisterUIIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            ScreenHeader(
                titleRes = R.string.auth_create_account,
                subtitleRes = R.string.auth_already_have_account,
                onBackClick = onNavigateBack,
                modifier = Modifier.padding(top = 8.dp),
            )

            SectionTitle(
                textRes = R.string.auth_first_name,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
            )

            AuthTextField(
                value = state.firstName,
                onValueChange = { onIntent(RegisterUIIntent.FirstNameChanged(it)) },
                labelRes = R.string.auth_first_name,
                leadingIcon = Icons.Filled.Person,
                errorRes = state.firstNameErrorRes,
                keyboardType = KeyboardType.Text,
            )

            AuthTextField(
                value = state.lastName,
                onValueChange = { onIntent(RegisterUIIntent.LastNameChanged(it)) },
                labelRes = R.string.auth_last_name,
                leadingIcon = Icons.Filled.Person,
                errorRes = state.lastNameErrorRes,
                keyboardType = KeyboardType.Text,
                modifier = Modifier.padding(top = 16.dp),
            )

            AuthTextField(
                value = state.phoneNumber,
                onValueChange = { onIntent(RegisterUIIntent.PhoneChanged(it)) },
                labelRes = R.string.auth_phone,
                leadingIcon = Icons.Filled.Phone,
                errorRes = state.phoneErrorRes,
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.padding(top = 16.dp),
            )

            AuthTextField(
                value = state.email,
                onValueChange = { onIntent(RegisterUIIntent.EmailChanged(it)) },
                labelRes = R.string.auth_email,
                leadingIcon = Icons.Filled.Email,
                errorRes = state.emailErrorRes,
                keyboardType = KeyboardType.Email,
                modifier = Modifier.padding(top = 16.dp),
            )

            PasswordField(
                value = state.password,
                onValueChange = { onIntent(RegisterUIIntent.PasswordChanged(it)) },
                labelRes = R.string.auth_password,
                isVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                errorRes = state.passwordErrorRes,
                modifier = Modifier.padding(top = 16.dp),
            )

            MedsyButton(
                onClick = { onIntent(RegisterUIIntent.Submit) },
                modifier = Modifier.padding(top = 24.dp),
                isLoading = state.isLoading,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.auth_register_button),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            SignInFooter(
                onSignInClick = onNavigateToSignIn,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            )
        }
        }

        MedsySnackbarHost(hostState = snackbarHostState)
    }
}