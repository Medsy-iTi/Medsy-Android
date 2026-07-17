package com.medsy.data.profile.repository

import com.medsy.data.profile.mapper.toDomain
import com.medsy.data.profile.remote.ProfileRemoteDataSource
import com.medsy.data.profile.remote.dto.CustomerDto
import com.medsy.data.remote.network.ApiError
import com.medsy.data.remote.network.ApiResult
import com.medsy.domain.profile.model.Profile
import com.medsy.domain.profile.repository.ProfileRepository
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    override suspend fun getCurrentProfile(): Result<Profile> =
        remoteDataSource.getCurrentProfile().toDomainResult()

    override suspend fun updateCurrentProfile(
        homeAddress: String?,
        dob: String?,
    ): Result<Profile> = remoteDataSource.updateCurrentProfile(
        homeAddress = homeAddress,
        dob = dob,
    ).toDomainResult()

    private fun ApiResult<CustomerDto>.toDomainResult(): Result<Profile> = when (this) {
        is ApiResult.Success -> {
            val profile = data?.toDomain()
            if (profile != null) {
                Result.success(profile)
            } else {
                Result.failure(IllegalStateException())
            }
        }

        is ApiResult.Error -> Result.failure(error.toException())
    }

    private fun ApiError.toException(): Throwable = when (this) {
        ApiError.NoInternet -> IOException()
        ApiError.EmptyResponse -> IllegalStateException()
        is ApiError.Server -> IllegalStateException(message)
        is ApiError.Unknown -> IllegalStateException(message)
    }
}
