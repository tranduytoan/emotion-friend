package com.emotionfriend.domain.model

data class PracticeAttempt(
    val id: String,
    val childId: String,
    val taskType: String,
    val promptId: String,
    val selectedEmotion: EmotionType?,
    val correctEmotion: EmotionType?,
    val isCorrect: Boolean?,
    val createdAt: Long
)
