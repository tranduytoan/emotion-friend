package com.emotionfriend

import android.app.Application
import com.emotionfriend.data.seed.SeedDataInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EmotionFriendApplication : Application() {

    @Inject lateinit var seedDataInitializer: SeedDataInitializer

    override fun onCreate() {
        super.onCreate()
        // Seed Room from JSON assets if tables are empty (no-op on subsequent launches).
        seedDataInitializer.initialize()
    }
}
