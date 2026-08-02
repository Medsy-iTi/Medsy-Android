package com.medsy.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import com.medsy.designsystem.util.isNetworkAvailable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.R
import kotlinx.coroutines.launch

@Composable
fun MedsyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    snackbarHostState: androidx.compose.material3.SnackbarHostState? = null,
    colors: androidx.compose.material3.ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ),
    content: @Composable () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
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
        content()
    }
}
