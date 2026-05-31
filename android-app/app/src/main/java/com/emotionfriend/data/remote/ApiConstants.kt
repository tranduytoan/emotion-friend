package com.emotionfriend.data.remote

import com.emotionfriend.core.config.AppConfig

/**
 * Remote API path constants.
 *
 * The base URL is sourced from [AppConfig] so it can be changed in one place.
 * Path constants stay here because they are data-layer concerns.
 */
object ApiConstants {
    /** Delegates to [AppConfig.BASE_URL] — change the URL there, not here. */
    val BASE_URL: String get() = AppConfig.BASE_URL

    const val PATH_EMOTIONS          = "/api/emotions"
    const val PATH_SCENARIOS         = "/api/scenarios"
    const val PATH_JOURNAL_ENTRIES   = "/api/journal-entries"
    const val PATH_PRACTICE_ATTEMPTS = "/api/practice-attempts"
    const val PATH_PROGRESS          = "/api/progress"
    const val PATH_STORIES           = "/api/stories"
    const val PATH_TOPICS            = "/api/topics"

    // ── Nghĩa's backend endpoints (P7) ─────────────────────────────────────
    /** GET  /api/situations   — all practice situations grouped by emotion. */
    const val PATH_SITUATIONS  = "/api/situations"
    /** POST /api/emotion-log  — submit an emotion log entry. */
    const val PATH_EMOTION_LOG = "/api/emotion-log"

    // ── Auth endpoints ───────────────────────────────────────────────────────
    const val PATH_AUTH_LOGIN           = "/api/auth/login"
    const val PATH_AUTH_REGISTER        = "/api/auth/register"
    const val PATH_AUTH_FORGOT_PASSWORD = "/api/auth/forgot-password"
    const val PATH_AUTH_VERIFY_EMAIL    = "/api/auth/verify-email"

    // ── Sync endpoints ───────────────────────────────────────────────────────
    const val PATH_SYNC_PULL = "/api/sync/pull"
    const val PATH_SYNC_PUSH = "/api/sync/push"
}
