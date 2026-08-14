package com.medsy.data.aichat.di

import com.medsy.data.aichat.repository.AiChatRepositoryImpl
import com.medsy.domain.aichat.repository.AiChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiChatDataModule {
    @Binds
    @Singleton
    abstract fun bindAiChatRepository(
        implementation: AiChatRepositoryImpl,
    ): AiChatRepository
}
