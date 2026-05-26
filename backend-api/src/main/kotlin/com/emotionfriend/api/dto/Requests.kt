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
    val promptEmotion: String? = null,
)

/** Request body for the mock camera expression-practice endpoint. */
@Serializable
data class ExpressionPracticeRequest(
    val childId: String,
    /** The emotion that was prompted (e.g. "HAPPY"). */
    val promptedEmotion: String,
    /** Optional client-side detection result — may be absent when mocking. */
    val detectedEmotion: String? = null,
)
