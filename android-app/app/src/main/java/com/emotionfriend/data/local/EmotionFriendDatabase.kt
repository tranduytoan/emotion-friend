package com.emotionfriend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        EmotionCardEntity::class,
        ScenarioLessonEntity::class,
        JournalEntryEntity::class,
        PracticeAttemptEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EmotionFriendDatabase : RoomDatabase() {

    abstract fun emotionCardDao(): EmotionCardDao
    abstract fun scenarioLessonDao(): ScenarioLessonDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun practiceAttemptDao(): PracticeAttemptDao

    companion object {
        const val DATABASE_NAME = "emotion_friend.db"
    }
}
