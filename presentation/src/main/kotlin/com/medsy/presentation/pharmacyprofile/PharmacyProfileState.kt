package com.medsy.presentation.pharmacyprofile

import androidx.annotation.StringRes
import com.medsy.domain.pharmacyprofile.model.PharmacyProfile

data class PharmacyProfileState(
    val isLoading: Boolean = true,
    val pharmacy: PharmacyProfile? = null,
    @StringRes val errorMessageRes: Int? = null,
)
