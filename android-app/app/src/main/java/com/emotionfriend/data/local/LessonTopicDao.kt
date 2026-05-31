package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonTopicDao {

    @Query("SELECT * FROM lesson_topics ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<LessonTopicEntity>>

    @Upsert
    suspend fun upsertAll(topics: List<LessonTopicEntity>)
}
