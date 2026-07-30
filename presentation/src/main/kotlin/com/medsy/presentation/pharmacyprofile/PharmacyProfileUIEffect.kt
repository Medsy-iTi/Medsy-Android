package com.medsy.presentation.pharmacyprofile

sealed interface PharmacyProfileUIEffect {
    data object NavigateBack : PharmacyProfileUIEffect
    data class DialPhone(val phoneNumber: String) : PharmacyProfileUIEffect
    data class OpenDirections(
        val latitude: Double,
        val longitude: Double,
    ) : PharmacyProfileUIEffect
}
