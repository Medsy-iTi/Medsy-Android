package com.medsy.presentation.prescription

sealed interface PrescriptionUIEffect {
    data class LaunchCamera(val uri: String) : PrescriptionUIEffect
    data object LaunchGallery : PrescriptionUIEffect
    data object NavigateBack : PrescriptionUIEffect
    data object NavigateHome : PrescriptionUIEffect
    data object NavigateCart : PrescriptionUIEffect
    data object PrescriptionAttached : PrescriptionUIEffect
    data class ShowMessage(val messageRes: Int) : PrescriptionUIEffect
}
