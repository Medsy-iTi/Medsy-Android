package com.medsy.data.reminders.di

import com.medsy.data.reminders.repository.MedicationReminderRepositoryImpl
import com.medsy.domain.reminders.repository.MedicationReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MedicationReminderDataModule {
    @Binds
    @Singleton
    abstract fun bindMedicationReminderRepository(
        implementation: MedicationReminderRepositoryImpl,
    ): MedicationReminderRepository
}
