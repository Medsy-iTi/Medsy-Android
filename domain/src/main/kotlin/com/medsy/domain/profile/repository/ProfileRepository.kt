package com.medsy.domain.profile.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.profile.model.Profile

interface ProfileRepository {

    suspend fun getCurrentProfile(): MedsyResult<Profile, MedsyError.Remote>

    suspend fun updateCurrentProfile(
        homeAddress: String?,
        dob: String?,
    ): MedsyResult<Profile, MedsyError.Remote>
}
