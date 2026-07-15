package com.medsy.presentation.auth.register.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.medsy.presentation.R

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    errorRes: Int? = null,
) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        labelRes = labelRes,
        leadingIcon = Icons.Filled.Lock,
        modifier = modifier,
        errorRes = errorRes,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = stringResource(R.string.content_desc_toggle_password_visibility),
                )
            }
        },
    )
}
