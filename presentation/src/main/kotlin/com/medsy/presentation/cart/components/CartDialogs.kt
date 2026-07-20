package com.medsy.presentation.cart.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.medsy.presentation.R

@Composable
internal fun CartClearDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cart_clear_title)) },
        text = { Text(stringResource(R.string.cart_clear_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.cart_clear_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cart_cancel))
            }
        },
    )
}

@Composable
internal fun CartNoteDialog(
    note: String,
    onNoteChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cart_note_title)) },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChanged,
                placeholder = { Text(stringResource(R.string.cart_note_hint)) },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.cart_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cart_cancel))
            }
        },
    )
}
