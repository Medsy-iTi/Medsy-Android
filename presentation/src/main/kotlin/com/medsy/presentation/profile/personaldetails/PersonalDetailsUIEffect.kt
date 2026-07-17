package com.medsy.presentation.profile.personaldetails

sealed interface PersonalDetailsUIEffect {
    data object SaveSucceeded : PersonalDetailsUIEffect
    data class SaveFailed(val message: String?) : PersonalDetailsUIEffect
}
