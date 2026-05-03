package com.emotionfriend.core.di

import com.emotionfriend.data.repository.EmotionRepository
import com.emotionfriend.data.repository.JournalRepository
import com.emotionfriend.data.repository.LocalEmotionRepository
import com.emotionfriend.data.repository.LocalJournalRepository
import com.emotionfriend.data.repository.LocalPracticeRepository
import com.emotionfriend.data.repository.LocalProgressRepository
import com.emotionfriend.data.repository.LocalScenarioRepository
import com.emotionfriend.data.repository.PracticeRepository
import com.emotionfriend.data.repository.ProgressRepository
import com.emotionfriend.data.repository.ScenarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmotionRepository(impl: LocalEmotionRepository): EmotionRepository

    @Binds
    @Singleton
    abstract fun bindScenarioRepository(impl: LocalScenarioRepository): ScenarioRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(impl: LocalJournalRepository): JournalRepository

    @Binds
    @Singleton
    abstract fun bindPracticeRepository(impl: LocalPracticeRepository): PracticeRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: LocalProgressRepository): ProgressRepository
}
