package com.medsy.domain.profile.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.model.UpdateProfileParams
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeProfile(): Flow<Profile?>

    suspend fun getCurrentProfile(): MedsyResult<Profile, MedsyError.Remote>

    suspend fun updateCurrentProfile(
        params: UpdateProfileParams,
    ): MedsyResult<Profile, MedsyError.Remote>
}
