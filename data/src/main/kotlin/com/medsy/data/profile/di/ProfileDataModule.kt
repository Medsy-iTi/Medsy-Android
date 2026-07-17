package com.medsy.data.profile.di

import com.medsy.data.profile.repository.ProfileRepositoryImpl
import com.medsy.domain.profile.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileDataModule {

    @Binds
    abstract fun bindProfileRepository(
        implementation: ProfileRepositoryImpl,
    ): ProfileRepository
}
