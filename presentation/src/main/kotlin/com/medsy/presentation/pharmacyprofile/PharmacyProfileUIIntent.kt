package com.medsy.presentation.pharmacyprofile

sealed interface PharmacyProfileUIIntent {
    data object BackClicked : PharmacyProfileUIIntent
    data object RetryClicked : PharmacyProfileUIIntent
    data object CallClicked : PharmacyProfileUIIntent
    data object DirectionsClicked : PharmacyProfileUIIntent
}
