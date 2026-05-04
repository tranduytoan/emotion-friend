package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class EmotionCard(
    val id: String,
    val emotionType: EmotionType,
    val emoji: String,
    val label: String,
    val description: String,
)
