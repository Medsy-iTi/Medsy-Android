package com.medsy.presentation.profile.personaldetails

sealed interface PersonalDetailsUIIntent {
    data object Retry : PersonalDetailsUIIntent
    data object EditClicked : PersonalDetailsUIIntent
    data object StartEditingAddress : PersonalDetailsUIIntent
    data object CancelEditClicked : PersonalDetailsUIIntent
    data class FirstNameChanged(val value: String) : PersonalDetailsUIIntent
    data class LastNameChanged(val value: String) : PersonalDetailsUIIntent
    data class HomeAddressChanged(val value: String) : PersonalDetailsUIIntent
    data class DobSelected(val value: String) : PersonalDetailsUIIntent
    data object LocationPickerClicked : PersonalDetailsUIIntent
    data object LocationPickerDismissed : PersonalDetailsUIIntent
    data class LocationSelected(
        val latitude: Double,
        val longitude: Double,
    ) : PersonalDetailsUIIntent
    data object SaveClicked : PersonalDetailsUIIntent
}
