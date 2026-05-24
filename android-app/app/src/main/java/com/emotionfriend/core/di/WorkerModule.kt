package com.emotionfriend.core.di

import androidx.work.WorkerFactory
import com.emotionfriend.data.sync.SyncWorker
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import androidx.hilt.work.HiltWorkerFactory
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides [HiltWorkerFactory] so WorkManager can inject [SyncWorker] via Hilt.
 *
 * **Important**: `WorkManager` must be configured with this factory.
 * See [EmotionFriendApplication] where `Configuration.Builder` sets it.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideWorkerFactory(factory: HiltWorkerFactory): WorkerFactory = factory
}
