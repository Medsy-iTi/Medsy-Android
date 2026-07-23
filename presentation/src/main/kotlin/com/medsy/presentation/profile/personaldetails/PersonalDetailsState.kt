package com.medsy.presentation.profile.personaldetails

import com.medsy.domain.profile.model.Profile

data class PersonalDetailsState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val hasLoadError: Boolean = false,
    val loadErrorMessageRes: Int? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isMapPickerVisible: Boolean = false,
    val draftFirstName: String = "",
    val draftLastName: String = "",
    val draftHomeAddress: String = "",
    val draftDob: String = "",
    val draftLatitude: Double? = null,
    val draftLongitude: Double? = null,
) {
    val isFirstNameValid: Boolean
        get() = draftFirstName.isNotBlank()

    val isLastNameValid: Boolean
        get() = draftLastName.isNotBlank()

    val hasChanges: Boolean
        get() = profile?.let {
            draftFirstName != it.firstName ||
                draftLastName != it.lastName ||
                draftHomeAddress != it.homeAddress.orEmpty() ||
                draftDob != it.dob.orEmpty() ||
                draftLatitude != it.latitude ||
                draftLongitude != it.longitude
        } == true
}
