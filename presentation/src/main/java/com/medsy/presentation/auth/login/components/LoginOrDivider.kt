package com.medsy.presentation.auth.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.BorderGray
import com.medsy.designsystem.ui.theme.SecondaryText
import com.medsy.presentation.R
import com.medsy.presentation.auth.login.LoginConstants

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
