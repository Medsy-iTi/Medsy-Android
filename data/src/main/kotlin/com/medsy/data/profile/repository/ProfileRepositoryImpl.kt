package com.medsy.data.profile.repository

import com.medsy.data.profile.mapper.toDomain
import com.medsy.data.profile.mapper.toDto
import com.medsy.data.profile.remote.ProfileRemoteDataSource
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.model.UpdateProfileParams
import com.medsy.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    private var cachedProfile: Profile? = null

    override suspend fun getCurrentProfile(): MedsyResult<Profile, MedsyError.Remote> {
        cachedProfile?.let { return MedsyResult.Success(it) }
        return remoteDataSource.getCurrentProfile().map { 
            it.toDomain().also { profile -> cachedProfile = profile }
        }
    }

    override suspend fun updateCurrentProfile(
        params: UpdateProfileParams,
    ): MedsyResult<Profile, MedsyError.Remote> = remoteDataSource.updateCurrentProfile(
        request = params.toDto(),
    ).map { 
        it.toDomain().also { profile -> cachedProfile = profile }
    }
}
