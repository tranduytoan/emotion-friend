package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionCardDao {

    @Query("SELECT * FROM emotion_cards ORDER BY label ASC")
    fun getAll(): Flow<List<EmotionCardEntity>>

    @Query("SELECT * FROM emotion_cards WHERE id = :id")
    suspend fun getById(id: String): EmotionCardEntity?

    @Upsert
    suspend fun upsertAll(cards: List<EmotionCardEntity>)

    @Upsert
    suspend fun upsert(card: EmotionCardEntity)
}
