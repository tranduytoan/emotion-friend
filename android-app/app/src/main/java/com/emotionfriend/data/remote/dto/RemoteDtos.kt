package com.emotionfriend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors backend ApiResponse<T> envelope. */
@Serializable
data class ApiResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
)

// ── Response DTOs ─────────────────────────────────────────────────────────────

@Serializable
data class EmotionCardDto(
    val id: Int,
    @SerialName("emotionType") val emotionType: String,
    val emoji: String,
    val label: String,
    val description: String,
)

@Serializable
data class ScenarioLessonDto(
    val id: Int,
    val title: String,
    val situation: String,
    val options: List<String>,       // EmotionType codes
    val correctEmotion: String,      // EmotionType code of correct answer
    val explanation: String,
)

@Serializable
data class JournalEntryDto(
    val id: Long,
    val childId: String,
    val emotionType: String,
    val note: String? = null,
    val createdAt: String,
)

@Serializable
data class PracticeAttemptDto(
    val id: Long,
    val childId: String,
    val scenarioId: Int? = null,
    val isCorrect: Boolean,
    val promptEmotion: String? = null,
    val createdAt: String,
)

@Serializable
data class ProgressSummaryDto(
    val childId: String,
    val completedLessons: Int,
    val accuracyRate: Float,
    val journalCount: Int,
    val mostMistakenEmotion: String? = null,
)

// ── Request DTOs ──────────────────────────────────────────────────────────────

@Serializable
data class CreateJournalEntryRequest(
    val childId: String,
    val emotionType: String,
    val note: String,
)

@Serializable
data class CreatePracticeAttemptRequest(
    val childId: String,
    val scenarioId: Int? = null,
    val isCorrect: Boolean,
    val promptEmotion: String? = null,
)

// ── Nghĩa's backend DTOs (P7 — plain JSON, no ApiResponseDto envelope) ───────

/** Mirrors backend Situation model — /api/situations. */
@Serializable
data class SituationDto(
    val id: Int,
    val emotionId: Int,
    val title: String,
    val description: String,
)

/** Mirrors backend EmotionLog response — /api/emotion-log. */
@Serializable
data class EmotionLogDto(
    val id: Int,
    val userId: Int,
    val emotionId: Int,
    val note: String? = null,
    val createdAt: String,
)

/** Request body for POST /api/emotion-log. */
@Serializable
data class CreateEmotionLogRequest(
    val userId: Int,
    val emotionId: Int,
    val note: String? = null,
)

/** Mirrors backend Story model — GET /api/stories. */
@Serializable
data class StoryDto(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val imageUrl: String? = null,
    val sortOrder: Int = 0,
)

// ── Auth DTOs ──────────────────────────────────────────────────────────────────

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val role: String = "CHILD",
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

@Serializable
data class AuthResponseDto(
    val userId: Long,
    val email: String,
    val displayName: String,
    val role: String,
    val token: String,
    val message: String = "OK",
)

// ── Sync DTOs ──────────────────────────────────────────────────────────────────

@Serializable
data class SyncPullResponseDto(
    val emotions: List<EmotionCardDto> = emptyList(),
    val serverTimestamp: Long = 0L,
)

@Serializable
data class JournalSyncEntry(
    val localId: String,
    val emotionId: Int,
    val note: String? = null,
    val createdAt: Long,
)

@Serializable
data class SyncPushRequestDto(
    val userId: Long,
    val journalEntries: List<JournalSyncEntry> = emptyList(),
)

@Serializable
data class SyncPushResponseDto(
    val accepted: Int,
    val rejected: Int,
    val serverTimestamp: Long = 0L,
)
