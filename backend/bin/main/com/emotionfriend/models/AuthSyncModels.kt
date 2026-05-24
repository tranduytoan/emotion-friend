package com.emotionfriend.models

import kotlinx.serialization.Serializable

// ── Auth models ───────────────────────────────────────────────────────────────

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
    val role: String = "CHILD",   // CHILD | PARENT | THERAPIST
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

@Serializable
data class AuthResponse(
    val userId: Long,
    val email: String,
    val displayName: String,
    val role: String,
    val token: String,
    val message: String = "OK",
)

@Serializable
data class MessageResponse(
    val message: String,
    val success: Boolean = true,
)

// ── Sync models ───────────────────────────────────────────────────────────────

@Serializable
data class SyncPullResponse(
    val emotions: List<Emotion> = emptyList(),
    val situations: List<Situation> = emptyList(),
    val serverTimestamp: Long = System.currentTimeMillis(),
)

@Serializable
data class SyncPushRequest(
    val userId: Long,
    val journalEntries: List<JournalSyncEntry> = emptyList(),
    val practiceAttempts: List<PracticeAttemptSyncEntry> = emptyList(),
)

@Serializable
data class JournalSyncEntry(
    val localId: String,
    val emotionId: Int,
    val note: String? = null,
    val createdAt: Long,
)

@Serializable
data class PracticeAttemptSyncEntry(
    val localId: String,
    val situationId: Int,
    val isCorrect: Boolean,
    val createdAt: Long,
)

@Serializable
data class SyncPushResponse(
    val accepted: Int,
    val rejected: Int,
    val serverTimestamp: Long = System.currentTimeMillis(),
)
