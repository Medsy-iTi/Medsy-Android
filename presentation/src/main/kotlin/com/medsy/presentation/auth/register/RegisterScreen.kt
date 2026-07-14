package com.medsy.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.feature.createaccount.components.PrimaryButton
import com.app.feature.createaccount.components.SignInFooter
import com.medsy.presentation.R
import com.medsy.presentation.auth.register.components.AuthTextField
import com.medsy.presentation.auth.register.components.PasswordField
import com.medsy.presentation.auth.register.components.ScreenHeader
import com.medsy.presentation.auth.register.components.SectionTitle
import com.medsy.presentation.auth.register.components.TermsCheckboxRow
import kotlinx.coroutines.flow.collectLatest


@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RegisterUIEffect.NavigateBack -> onNavigateBack()
                RegisterUIEffect.NavigateToSignIn -> onNavigateToSignIn()
                RegisterUIEffect.NavigateToHome -> onNavigateToHome()
                is RegisterUIEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(context.getString(effect.messageRes))

                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        CreateAccountContent(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}


@Composable
fun CreateAccountContent(
    state: RegisterUIState,
    onIntent: (RegisterUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        ScreenHeader(
            titleRes = R.string.create_account_title,
            subtitleRes = R.string.create_account_subtitle,
            onBackClick = { onIntent(RegisterUIIntent.BackClicked) },
            modifier = Modifier.padding(top = 8.dp),
        )

        SectionTitle(
            textRes =  R.string.create_account_section_personal_info,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
        )

        AuthTextField(
            value = state.fullName,
            onValueChange = { onIntent(RegisterUIIntent.FullNameChanged(it)) },
            labelRes = R.string.field_full_name,
            leadingIcon =  Icons.Filled.Person,
            errorRes = state.fullNameError,
            keyboardType = KeyboardType.Text,
        )

        AuthTextField(
            value = state.phoneNumber,
            onValueChange = { onIntent(RegisterUIIntent.PhoneNumberChanged(it)) },
            labelRes = R.string.field_phone_number,
            leadingIcon = Icons.Filled.Phone,
            errorRes = state.phoneNumberError,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.padding(top = 16.dp),
        )

        AuthTextField(
            value = state.email,
            onValueChange = { onIntent(RegisterUIIntent.EmailChanged(it)) },
            labelRes = R.string.field_email,
            leadingIcon = Icons.Filled.Email,
            errorRes = state.emailError,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(top = 16.dp),
        )

        PasswordField(
            value = state.password,
            onValueChange = { onIntent(RegisterUIIntent.PasswordChanged(it)) },
            labelRes = R.string.field_password,
            isVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(RegisterUIIntent.TogglePasswordVisibility) },
            errorRes = state.passwordError,
            modifier = Modifier.padding(top = 16.dp),
        )

        PasswordField(
            value = state.confirmPassword,
            onValueChange = { onIntent(RegisterUIIntent.ConfirmPasswordChanged(it)) },
            labelRes = R.string.field_confirm_password,
            isVisible = state.isConfirmPasswordVisible,
            onToggleVisibility = { onIntent(RegisterUIIntent.ToggleConfirmPasswordVisibility) },
            errorRes = state.confirmPasswordError,
            modifier = Modifier.padding(top = 16.dp),
        )

        TermsCheckboxRow(
            checked = state.isTermsAccepted,
            onCheckedChange = { onIntent(RegisterUIIntent.TermsAcceptedChanged(it)) },
            errorRes = state.termsError,
            modifier = Modifier.padding(top = 20.dp),
        )

        PrimaryButton(
            text = stringResource(R.string.button_create_account),
            onClick = { onIntent(RegisterUIIntent.SubmitClicked) },
            isLoading = state.isLoading,
            modifier = Modifier.padding(top = 24.dp),
        )

        SignInFooter(
            onSignInClick = { onIntent(RegisterUIIntent.SignInClicked) },
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
    }
}