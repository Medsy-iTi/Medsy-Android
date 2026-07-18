package com.medsy.presentation.profile.personaldetails

import com.medsy.domain.profile.model.Profile

data class PersonalDetailsState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val hasLoadError: Boolean = false,
    val loadErrorMessageRes: Int? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val draftHomeAddress: String = "",
    val draftDob: String = "",
) {
    val hasChanges: Boolean
        get() = profile?.let {
            draftHomeAddress != it.homeAddress.orEmpty() ||
                draftDob != it.dob.orEmpty()
        } == true
}
