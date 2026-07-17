package com.medsy.presentation.profile.personaldetails

sealed interface PersonalDetailsUIIntent {
    data object Retry : PersonalDetailsUIIntent
    data object EditClicked : PersonalDetailsUIIntent
    data object CancelEditClicked : PersonalDetailsUIIntent
    data class HomeAddressChanged(val value: String) : PersonalDetailsUIIntent
    data class DobSelected(val value: String) : PersonalDetailsUIIntent
    data object SaveClicked : PersonalDetailsUIIntent
}
