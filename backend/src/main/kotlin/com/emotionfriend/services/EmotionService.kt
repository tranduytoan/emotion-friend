package com.emotionfriend.services

import com.emotionfriend.models.Emotion
import com.emotionfriend.repositories.EmotionRepository

class EmotionService(private val repo: EmotionRepository) {
    fun getAll(): List<Emotion> = repo.findAll()
}
