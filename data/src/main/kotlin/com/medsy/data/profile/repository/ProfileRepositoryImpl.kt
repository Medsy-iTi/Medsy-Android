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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    private val _profileFlow = MutableStateFlow<Profile?>(null)

    override fun observeProfile(): Flow<Profile?> = _profileFlow.asStateFlow()

    override suspend fun getCurrentProfile(): MedsyResult<Profile, MedsyError.Remote> {
        _profileFlow.value?.let { return MedsyResult.Success(it) }
        return remoteDataSource.getCurrentProfile().map {
            it.toDomain().also { profile -> _profileFlow.value = profile }
        }
    }

    override suspend fun updateCurrentProfile(
        params: UpdateProfileParams,
    ): MedsyResult<Profile, MedsyError.Remote> = remoteDataSource.updateCurrentProfile(
        request = params.toDto(),
    ).map {
        it.toDomain().also { profile -> _profileFlow.value = profile }
    }
}
