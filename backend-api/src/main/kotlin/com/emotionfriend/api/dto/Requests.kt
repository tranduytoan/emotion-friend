package com.emotionfriend.api.dto

import com.emotionfriend.api.model.EmotionType
import kotlinx.serialization.Serializable

@Serializable
data class CreateJournalEntryRequest(
    val childId: String,
    val emotionType: EmotionType,
    val note: String,
)

@Serializable
data class CreatePracticeAttemptRequest(
    val childId: String,
    val scenarioId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
)
