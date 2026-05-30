package com.emotionfriend

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.emotionfriend.data.sync.SyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EmotionFriendApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: WorkerFactory
    @Inject lateinit var syncManager: SyncManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Data is loaded from backend via SyncWorker — no local seeding.
        syncManager.schedulePeriodicSync()
    }
}
