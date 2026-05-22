package com.emotionfriend.services

import com.emotionfriend.models.EmotionLog
import com.emotionfriend.repositories.EmotionLogRepository

class EmotionLogService(private val repo: EmotionLogRepository) {
    fun log(userId: Long, emotionId: Int, note: String?): EmotionLog =
        repo.insert(userId, emotionId, note)
}
