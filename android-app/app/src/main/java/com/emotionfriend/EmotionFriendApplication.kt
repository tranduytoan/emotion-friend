package com.emotionfriend

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.emotionfriend.data.seed.SeedDataInitializer
import com.emotionfriend.data.sync.SyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EmotionFriendApplication : Application(), Configuration.Provider {

    @Inject lateinit var seedDataInitializer: SeedDataInitializer
    @Inject lateinit var workerFactory: WorkerFactory
    @Inject lateinit var syncManager: SyncManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Seed Room from JSON assets if tables are empty (no-op on subsequent launches).
        seedDataInitializer.initialize()
        // Schedule periodic 30-min background sync (requires network).
        syncManager.schedulePeriodicSync()
    }
}
