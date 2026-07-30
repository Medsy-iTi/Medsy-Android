package com.medsy.data.pharmacyprofile.di

import com.medsy.data.pharmacyprofile.repository.PharmacyProfileRepositoryImpl
import com.medsy.domain.pharmacyprofile.repository.PharmacyProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PharmacyProfileDataModule {
    @Binds
    @Singleton
    abstract fun bindPharmacyProfileRepository(
        implementation: PharmacyProfileRepositoryImpl,
    ): PharmacyProfileRepository
}
