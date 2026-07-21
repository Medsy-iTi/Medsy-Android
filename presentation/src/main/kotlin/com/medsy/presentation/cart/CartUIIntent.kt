package com.medsy.presentation.cart

sealed interface CartUIIntent {
    data object CartOpened : CartUIIntent
    data object Refresh : CartUIIntent
    data object RetryClicked : CartUIIntent
    data class IncreaseQuantityClicked(val cartItemId: Long) : CartUIIntent
    data class DecreaseQuantityClicked(val cartItemId: Long) : CartUIIntent
    data class RemoveItemClicked(val cartItemId: Long) : CartUIIntent
    data object ClearCartClicked : CartUIIntent
    data object ClearCartConfirmed : CartUIIntent
    data object ClearCartDismissed : CartUIIntent
    data object AddPrescriptionClicked : CartUIIntent
    data object RemovePrescriptionClicked : CartUIIntent
    data object AddNoteClicked : CartUIIntent
    data class NoteChanged(val note: String) : CartUIIntent
    data object SaveNoteClicked : CartUIIntent
    data object NoteDialogDismissed : CartUIIntent
}
