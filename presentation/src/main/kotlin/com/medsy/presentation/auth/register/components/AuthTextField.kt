package com.medsy.presentation.auth.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyTextField


@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    errorRes: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        MedsyTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(stringResource(labelRes)) },
            leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )

        if (errorRes != null) {
            Text(
                text = stringResource(errorRes),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            )
        }
    }
}