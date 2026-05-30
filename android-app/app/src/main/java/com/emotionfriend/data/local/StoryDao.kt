package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {

    @Query("SELECT * FROM stories ORDER BY id ASC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): StoryEntity?

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(stories: List<StoryEntity>)
}
