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
}
