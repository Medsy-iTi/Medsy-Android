package com.medsy.domain.profile.repository

import com.medsy.domain.profile.model.Profile

interface ProfileRepository {

    suspend fun getCurrentProfile(): Result<Profile>

    suspend fun updateCurrentProfile(
        homeAddress: String?,
        dob: String?,
    ): Result<Profile>
}
