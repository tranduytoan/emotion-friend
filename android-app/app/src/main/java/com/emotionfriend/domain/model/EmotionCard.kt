package com.emotionfriend.domain.model

data class EmotionCard(
    val id: String,
    val label: String,
    val emoji: String,
    val type: EmotionType,
    val description: String
)
