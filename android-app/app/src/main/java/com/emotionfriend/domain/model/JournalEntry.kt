package com.emotionfriend.domain.model

data class JournalEntry(
    val id: String,
    val childId: String,
    val emotionType: EmotionType,
    val note: String?,
    val createdAt: Long
)
