package com.medsy.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.cart.usecase.ObserveCartItemCountUseCase
import com.medsy.domain.cart.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartBadgeViewModel @Inject constructor(
    observeCartItemCount: ObserveCartItemCountUseCase,
    private val cartRepository: CartRepository
) : ViewModel() {

    init {
        // Fetch the initial cart to sync the cart count from backend
        viewModelScope.launch {
            cartRepository.getCart()
        }
    }

    val cartItemCount: StateFlow<Int> = observeCartItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )
}
