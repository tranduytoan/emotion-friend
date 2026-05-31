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
    val scenarioId: Int? = null,
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

@Serializable
data class AuthLoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthRegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
)

@Serializable
data class AuthForgotPasswordRequest(
    val email: String,
)

@Serializable
data class AuthVerifyEmailRequest(
    val email: String,
    val code: String,
)
