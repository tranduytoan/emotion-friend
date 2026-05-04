package com.emotionfriend.api.repository

import com.emotionfriend.api.model.EmotionCard

interface EmotionRepository {
    suspend fun getAll(): List<EmotionCard>
    suspend fun getById(id: String): EmotionCard?
}
