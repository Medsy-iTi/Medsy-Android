package com.medsy.presentation.auth.login.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.medsy.designsystem.components.MedsyTextField
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.R

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
