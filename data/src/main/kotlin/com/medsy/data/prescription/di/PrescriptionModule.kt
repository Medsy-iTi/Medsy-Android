package com.medsy.data.prescription.di

import com.medsy.data.prescription.repository.PrescriptionRepositoryImpl
import com.medsy.domain.prescription.repository.PrescriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrescriptionModule {

    @Binds
    @Singleton
    abstract fun bindPrescriptionRepository(
        repository: PrescriptionRepositoryImpl,
    ): PrescriptionRepository
}
