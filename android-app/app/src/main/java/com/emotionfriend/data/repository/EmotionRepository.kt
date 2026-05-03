package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import kotlinx.coroutines.flow.Flow

interface EmotionRepository {
    fun getAll(): Flow<List<EmotionCard>>
    suspend fun getById(id: String): EmotionCard?
    suspend fun upsertAll(cards: List<EmotionCard>)
    fun getByType(type: EmotionType): Flow<List<EmotionCard>>
}
