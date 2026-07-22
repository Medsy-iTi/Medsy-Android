package com.medsy.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.model.Cart
import com.medsy.domain.cart.usecase.ClearCartUseCase
import com.medsy.domain.cart.usecase.GetCartUseCase
import com.medsy.domain.cart.usecase.ObserveCartDraftUseCase
import com.medsy.domain.cart.usecase.RemoveCartItemUseCase
import com.medsy.domain.cart.usecase.RemoveCartPrescriptionUseCase
import com.medsy.domain.cart.usecase.SetCartItemQuantityUseCase
import com.medsy.domain.cart.usecase.UpdateCartNoteUseCase
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCart: GetCartUseCase,
    private val setItemQuantity: SetCartItemQuantityUseCase,
    private val removeItem: RemoveCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val observeCartDraft: ObserveCartDraftUseCase,
    private val updateCartNote: UpdateCartNoteUseCase,
    private val removeCartPrescription: RemoveCartPrescriptionUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state = _state.asStateFlow()

    private val _effect = Channel<CartUIEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private fun sendEffect(effect: CartUIEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    init {
        viewModelScope.launch {
            observeCartDraft().collect { draft ->
                _state.update {
                    it.copy(
                        draft = draft,
                        noteInput = if (it.isNoteDialogVisible) {
                            it.noteInput
                        } else {
                            draft.pharmacistNote
                        },
                    )
                }
            }
        }
    }

    fun onIntent(intent: CartUIIntent) {
        when (intent) {
            CartUIIntent.CartOpened -> loadCart()
            CartUIIntent.Refresh -> refreshCart()
            CartUIIntent.RetryClicked -> loadCart()
            is CartUIIntent.IncreaseQuantityClicked -> changeQuantity(intent.cartItemId, 1)
            is CartUIIntent.DecreaseQuantityClicked -> changeQuantity(intent.cartItemId, -1)
            is CartUIIntent.RemoveItemClicked -> removeItem(intent.cartItemId)
            CartUIIntent.ClearCartClicked ->
                _state.update { it.copy(isClearDialogVisible = true) }

            CartUIIntent.ClearCartConfirmed -> clearCart()
            CartUIIntent.ClearCartDismissed ->
                _state.update { it.copy(isClearDialogVisible = false) }

            CartUIIntent.AddPrescriptionClicked -> sendEffect(CartUIEffect.OpenPrescription)
            CartUIIntent.RemovePrescriptionClicked -> removePrescription()
            CartUIIntent.AddNoteClicked -> _state.update {
                it.copy(
                    isNoteDialogVisible = true,
                    noteInput = it.draft.pharmacistNote,
                )
            }

            is CartUIIntent.NoteChanged -> _state.update { it.copy(noteInput = intent.note) }
            CartUIIntent.SaveNoteClicked -> saveNote()
            CartUIIntent.NoteDialogDismissed -> _state.update {
                it.copy(
                    isNoteDialogVisible = false,
                    noteInput = it.draft.pharmacistNote,
                )
            }

            CartUIIntent.SubmitCartClicked -> sendEffect(CartUIEffect.OpenCheckout)
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = false,
                    errorMessageRes = null,
                )
            }
            getCart()
                .onSuccess(::applyCart)
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }

    private fun refreshCart() {
        if (_state.value.isLoading || _state.value.isRefreshing) return
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            getCart()
                .onSuccess { cart ->
                    applyCart(cart)
                }
                .onError { error ->
                    _state.update { it.copy(isRefreshing = false) }
                    sendEffect(CartUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun changeQuantity(
        cartItemId: Long,
        delta: Int,
    ) {
        val item = _state.value.items.firstOrNull { it.id == cartItemId } ?: return
        if (cartItemId in _state.value.updatingItemIds) return
        val quantity = (item.quantity + delta).coerceAtLeast(1)
        if (quantity == item.quantity) return

        updateItem(cartItemId) {
            setItemQuantity(cartItemId, quantity)
        }
    }

    private fun removeItem(cartItemId: Long) {
        if (cartItemId in _state.value.updatingItemIds) return
        updateItem(cartItemId) {
            removeItem.invoke(cartItemId)
        }
    }

    private fun updateItem(
        cartItemId: Long,
        operation: suspend () -> MedsyResult<Cart, MedsyError.Remote>,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(updatingItemIds = it.updatingItemIds + cartItemId) }
            operation()
                .onSuccess { cart -> applyCart(cart) }
                .onError { error ->
                    _state.update { it.copy(updatingItemIds = it.updatingItemIds - cartItemId) }
                    sendEffect(CartUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun clearCart() {
        if (_state.value.isClearing) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isClearing = true,
                    isClearDialogVisible = false,
                )
            }
            clearCart.invoke()
                .onSuccess {
                    _state.update {
                        it.copy(
                            items = emptyList(),
                            totalPriceEgp = 0.0,
                            updatingItemIds = emptySet(),
                            isClearing = false,
                            errorMessageRes = null,
                        )
                    }
                    sendEffect(CartUIEffect.ShowMessage(R.string.cart_cleared))
                }
                .onError { error ->
                    _state.update { it.copy(isClearing = false) }
                    sendEffect(CartUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            updateCartNote(_state.value.noteInput.trim())
                .onSuccess {
                    _state.update { it.copy(isNoteDialogVisible = false) }
                    sendEffect(CartUIEffect.ShowMessage(R.string.cart_note_saved))
                }
                .onError { error ->
                    sendEffect(CartUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun removePrescription() {
        viewModelScope.launch {
            removeCartPrescription()
                .onSuccess {
                    sendEffect(CartUIEffect.ShowMessage(R.string.cart_prescription_removed))
                }
                .onError { error ->
                    sendEffect(CartUIEffect.ShowMessage(error.toMessageRes()))
                }
        }
    }

    private fun applyCart(cart: Cart) {
        _state.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                items = cart.items,
                totalPriceEgp = cart.totalPriceEgp,
                updatingItemIds = emptySet(),
                isClearing = false,
                errorMessageRes = null,
            )
        }
    }
}
