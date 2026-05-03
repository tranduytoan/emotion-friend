package com.emotionfriend.core.di

import android.content.Context
import androidx.room.Room
import com.emotionfriend.data.local.EmotionCardDao
import com.emotionfriend.data.local.EmotionFriendDatabase
import com.emotionfriend.data.local.JournalEntryDao
import com.emotionfriend.data.local.PracticeAttemptDao
import com.emotionfriend.data.local.ScenarioLessonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EmotionFriendDatabase =
        Room.databaseBuilder(
            context,
            EmotionFriendDatabase::class.java,
            EmotionFriendDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideEmotionCardDao(db: EmotionFriendDatabase): EmotionCardDao =
        db.emotionCardDao()

    @Provides
    fun provideScenarioLessonDao(db: EmotionFriendDatabase): ScenarioLessonDao =
        db.scenarioLessonDao()

    @Provides
    fun provideJournalEntryDao(db: EmotionFriendDatabase): JournalEntryDao =
        db.journalEntryDao()

    @Provides
    fun providePracticeAttemptDao(db: EmotionFriendDatabase): PracticeAttemptDao =
        db.practiceAttemptDao()
}
