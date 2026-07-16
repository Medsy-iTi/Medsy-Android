package com.medsy.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.designsystem.ui.theme.PrimaryText
import com.medsy.presentation.R
import com.medsy.presentation.auth.login.components.LoginOrDivider
import com.medsy.presentation.auth.login.components.LoginPasswordInput
import com.medsy.presentation.auth.login.components.LoginPhoneInput
import com.medsy.presentation.auth.login.components.LoginSocialButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
import com.medsy.designsystem.R as DesignR

@Composable
fun LoginRoot(
    openSignup: () -> Unit,
    openHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateHome -> openHome()
                is LoginEffect.ShowError    -> snackbarHostState.showError(
                    message = context.getString(effect.messageRes)
                )
            }
        }
    }

    LoginScreen(
        state = state,
        onIntent = viewModel::onIntent,
        openSignup = openSignup,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    openSignup: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(
                    horizontal = LoginConstants.ScreenPaddingHorizontal,
                    vertical = LoginConstants.ScreenPaddingVertical
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Spacer(modifier = Modifier.height(LoginConstants.TopSpacer))

        val logoRes = if (isDarkTheme) {
            DesignR.drawable.ic_logo_transparent_dark
        } else {
            DesignR.drawable.ic_logo_transparent
        }

        // Logo
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = stringResource(R.string.medsy_logo_content_desc),
            modifier = Modifier.width(LoginConstants.LogoWidth),
            contentScale = ContentScale.FillWidth
        )

        // Medesy Name
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(LoginConstants.SpacerLogoText))

        // Motto
        Text(
            text = stringResource(R.string.login_motto),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(LoginConstants.SpacerTextForm))

        com.medsy.designsystem.components.MedsyTextField(
            value = state.email,
            onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.auth_email)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null
                )
            },
            singleLine = true,
            errorRes = state.emailErrorRes
        )

        Spacer(modifier = Modifier.height(LoginConstants.SpacerInput))

        // Password Input
        LoginPasswordInput(
            password = state.password,
            onPasswordChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            errorRes = state.passwordErrorRes
        )

        Spacer(modifier = Modifier.height(LoginConstants.SpacerInput))

        // Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.login_forgot_password),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(LoginConstants.SpacerInputButton))

        // Login Button
        MedsyButton(
            onClick = { onIntent(LoginIntent.Submit) },
            isLoading = state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.login_button),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralWhite
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(LoginConstants.SpacerOr))

        // OR Divider
        LoginOrDivider()

        Spacer(modifier = Modifier.height(LoginConstants.SpacerOr))

        // Google Button
        LoginSocialButton(
            iconResId = DesignR.drawable.ic_google,
            text = stringResource(R.string.login_google),
            onClick = { }
        )

        Spacer(modifier = Modifier.height(LoginConstants.SpacerSocial))

        // Facebook Button
        LoginSocialButton(
            iconResId = DesignR.drawable.ic_facebook,
            text = stringResource(R.string.login_facebook),
            onClick = { }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Don't have an account
        val fullText = stringResource(
            R.string.login_no_account_full,
            stringResource(R.string.login_signup)
        )

        val annotated = buildAnnotatedString {
            val start = fullText.indexOf(stringResource(R.string.login_signup))

            append(fullText)

            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                start = start,
                end = start + stringResource(R.string.login_signup).length
            )
        }

        Text(
            text = annotated,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clickable { openSignup() }
        )
        } // end Column
        } // end Scaffold

        MedsySnackbarHost(hostState = snackbarHostState)
    } // end Box
}
