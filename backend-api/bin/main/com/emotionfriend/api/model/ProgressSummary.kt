package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ProgressSummary(
    val childId: String,
    val completedLessons: Int,
    val accuracyRate: Float,
    val journalCount: Int,
    val mostMistakenEmotion: EmotionType?,
)
