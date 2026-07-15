package com.medsy.presentation.auth.login.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyTextField
import com.medsy.designsystem.ui.theme.BorderGray
import com.medsy.designsystem.ui.theme.NeutralWhite
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.R
import com.medsy.presentation.auth.login.LoginConstants

@Composable
fun LoginPhoneInput(
    phone: String,
    onPhoneChange: (String) -> Unit
) {
    MedsyTextField(
        value = phone,
        onValueChange = onPhoneChange,
        placeholder = { Text(text = stringResource(R.string.login_phone_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Phone,
                contentDescription = null,
                tint = SecondaryText
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}

@Composable
fun LoginPasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    MedsyTextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text(text = stringResource(R.string.login_password_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = SecondaryText
            )
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = SecondaryText
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun LoginOrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.width(LoginConstants.DividerWidth),
            color = BorderGray
        )
        Text(
            text = stringResource(R.string.login_or),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SecondaryText,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.width(LoginConstants.DividerWidth),
            color = BorderGray
        )
    }
}

@Composable
fun LoginSocialButton(
    iconResId: Int,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(LoginConstants.SocialButtonHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(LoginConstants.SocialButtonCornerRadius),
        border = BorderStroke(1.dp, BorderGray),
        color = NeutralWhite
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(LoginConstants.IconSize)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                modifier = Modifier.width(LoginConstants.SocialButtonTextWidth), 
                textAlign = TextAlign.Start,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
