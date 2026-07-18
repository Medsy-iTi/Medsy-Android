package com.medsy.data.prescription.di

import com.medsy.data.prescription.mock.MockPrescriptionScenario
import com.medsy.data.prescription.repository.MockPrescriptionRepository
import com.medsy.domain.prescription.repository.PrescriptionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrescriptionModule {

    @Binds
    @Singleton
    abstract fun bindPrescriptionRepository(
        repository: MockPrescriptionRepository,
    ): PrescriptionRepository

    companion object {
        @Provides
        fun provideMockPrescriptionScenario(): MockPrescriptionScenario =
            MockPrescriptionScenario.SUCCESS
    }
}
