package com.medsy.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import com.medsy.designsystem.util.isNetworkAvailable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.medsy.designsystem.R
import kotlinx.coroutines.launch

@Composable
fun MedsyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    snackbarHostState: SnackbarHostState? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isEnabled = enabled && !isLoading
    Button(
        onClick = {
            if (!context.isNetworkAvailable()) {
                if (snackbarHostState != null) {
                    coroutineScope.launch {
                        snackbarHostState.showError(context.getString(R.string.designsystem_network_error))
                    }
                }
            } else {
                onClick()
            }
        },
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = colors,
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color =LocalContentColor.current,
                strokeWidth = 2.dp
            )
        } else {
            ProvideTextStyle(
                value = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            ) {
                content()
            }
        }
    }
}
