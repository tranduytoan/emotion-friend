package com.emotionfriend.domain.model

data class ProgressSummary(
    val completedLessons: Int,
    val accuracyRate: Float,
    val mostMistakenEmotion: EmotionType?,
    val journalCount: Int
)
