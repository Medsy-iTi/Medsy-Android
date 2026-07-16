package com.medsy.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SnackbarHost
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.feature.createaccount.components.SignInFooter
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R
import com.medsy.presentation.auth.register.components.AuthTextField
import com.medsy.presentation.auth.register.components.PasswordField
import com.medsy.presentation.auth.register.components.ScreenHeader
import com.medsy.presentation.auth.register.components.SectionTitle
import com.medsy.presentation.auth.register.components.TermsCheckboxRow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.NavigateToOtp -> onNavigateToOtp(effect.email)
            }
        }
    }

    RegisterScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToSignIn = onNavigateToSignIn
    )
}


@Composable
fun RegisterScreen(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.errorMessage, state.errorMessageRes) {
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage)
        } else if (state.errorMessageRes != null) {
            snackbarHostState.showSnackbar(context.getString(state.errorMessageRes))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        CreateAccountContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier
                .padding(paddingValues)
                .imePadding(),
            onNavigateBack = onNavigateBack,
            onNavigateToSignIn = onNavigateToSignIn
        )
    }
}


@Composable
fun CreateAccountContent(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        ScreenHeader(
            titleRes = R.string.auth_create_account,
            subtitleRes = R.string.auth_already_have_account,
            onBackClick = { onNavigateBack() },
            modifier = Modifier.padding(top = 8.dp),
        )

        SectionTitle(
            textRes =  R.string.auth_first_name,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
        )

        AuthTextField(
            value = state.firstName,
            onValueChange = { onIntent(RegisterIntent.FirstNameChanged(it)) },
            labelRes = R.string.auth_first_name,
            leadingIcon =  Icons.Filled.Person,
            errorRes = state.firstNameErrorRes,
            keyboardType = KeyboardType.Text,
        )

        AuthTextField(
            value = state.lastName,
            onValueChange = { onIntent(RegisterIntent.LastNameChanged(it)) },
            labelRes = R.string.auth_last_name,
            leadingIcon =  Icons.Filled.Person,
            errorRes = state.lastNameErrorRes,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.padding(top = 16.dp),
        )

        AuthTextField(
            value = state.phoneNumber,
            onValueChange = { onIntent(RegisterIntent.PhoneChanged(it)) },
            labelRes = R.string.auth_phone,
            leadingIcon = Icons.Filled.Phone,
            errorRes = state.phoneErrorRes,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.padding(top = 16.dp),
        )

        AuthTextField(
            value = state.email,
            onValueChange = { onIntent(RegisterIntent.EmailChanged(it)) },
            labelRes = R.string.auth_email,
            leadingIcon = Icons.Filled.Email,
            errorRes = state.emailErrorRes,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(top = 16.dp),
        )

        PasswordField(
            value = state.password,
            onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
            labelRes = R.string.auth_password,
            isVisible = passwordVisible,
            onToggleVisibility = { passwordVisible = !passwordVisible },
            errorRes = state.passwordErrorRes,
            modifier = Modifier.padding(top = 16.dp),
        )

        PasswordField(
            value = state.confirmPassword,
            onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
            labelRes = R.string.auth_confirm_password,
            isVisible = confirmPasswordVisible,
            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            errorRes = state.confirmPasswordErrorRes,
            modifier = Modifier.padding(top = 16.dp),
        )

        MedsyButton(
            onClick = { onIntent(RegisterIntent.Submit) },
            modifier = Modifier.padding(top = 24.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_register_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        SignInFooter(
            onSignInClick = onNavigateToSignIn,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
    }
}