package com.medsy.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showMessage
import kotlinx.coroutines.flow.collectLatest
import com.medsy.presentation.cart.components.CartClearDialog
import com.medsy.presentation.cart.components.CartContent
import com.medsy.presentation.cart.components.CartError
import com.medsy.presentation.cart.components.CartNoteDialog
import com.medsy.presentation.cart.components.CartShimmer

@Composable
fun CartRoot(
    onAddPrescription: () -> Unit,
    onMedicineSearch: () -> Unit,
    onOpenCartRequest: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.onIntent(CartUIIntent.CartOpened)
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                CartUIEffect.OpenPrescription -> onAddPrescription()
                CartUIEffect.OpenMakeRequest -> onOpenCartRequest()
                CartUIEffect.OpenMedicineSearch -> onMedicineSearch()
                is CartUIEffect.ShowMessage ->
                    snackbarHostState.showMessage(
                        context = context,
                        messageRes = effect.messageRes,
                        isSuccess = effect.isSuccess
                    )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CartScreen(
            state = state,
            onIntent = viewModel::onIntent,
        )
        MedsySnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CartScreen(
    state: CartState,
    onIntent: (CartUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> CartShimmer()

            state.errorMessageRes != null -> CartError(
                message = stringResource(state.errorMessageRes),
                onRetry = { onIntent(CartUIIntent.RetryClicked) },
                modifier = Modifier.align(Alignment.Center),
            )

            else -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(CartUIIntent.Refresh) },
            ) {
                CartContent(state = state, onIntent = onIntent)
            }
        }
    }

    if (state.isClearDialogVisible) {
        CartClearDialog(
            onConfirm = { onIntent(CartUIIntent.ClearCartConfirmed) },
            onDismiss = { onIntent(CartUIIntent.ClearCartDismissed) },
        )
    }

    if (state.itemToRemove != null) {
        com.medsy.designsystem.components.MedsyAlertDialog(
            onDismissRequest = { onIntent(CartUIIntent.RemoveItemDismissed) },
            onConfirm = { onIntent(CartUIIntent.RemoveItemConfirmed) },
            title = stringResource(com.medsy.presentation.R.string.cart_remove_item_title),
            description = stringResource(com.medsy.presentation.R.string.cart_remove_item_message),
            confirmText = stringResource(com.medsy.presentation.R.string.cart_remove_item_confirm),
            dismissText = stringResource(com.medsy.presentation.R.string.cart_cancel),
            isDestructive = true
        )
    }

    if (state.isNoteDialogVisible) {
        CartNoteDialog(
            note = state.noteInput,
            onNoteChanged = { onIntent(CartUIIntent.NoteChanged(it)) },
            onSave = { onIntent(CartUIIntent.SaveNoteClicked) },
            onDismiss = { onIntent(CartUIIntent.NoteDialogDismissed) },
        )
    }
}
