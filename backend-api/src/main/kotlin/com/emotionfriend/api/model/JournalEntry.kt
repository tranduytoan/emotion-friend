package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val id: Long = 0L,
    val childId: String,
    val emotionType: EmotionType,
    val note: String,
    val createdAt: String = "",
)
