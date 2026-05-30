package com.emotionfriend.api.service

import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.repository.EmotionRepository

class EmotionService(private val repo: EmotionRepository) {
    suspend fun getAll(): List<EmotionCard> = repo.getAll()
    suspend fun getById(id: Int): EmotionCard =
        repo.getById(id) ?: throw NoSuchElementException("Emotion '$id' not found")
}
