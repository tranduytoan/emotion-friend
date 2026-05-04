package com.emotionfriend.data.remote

/**
 * Central place for remote API configuration.
 * Replace BASE_URL with your backend address before enabling remote sync.
 * For local Docker Compose testing on an emulator, use http://10.0.2.2:8080
 */
object ApiConstants {
    // TODO: replace with actual backend URL or inject via BuildConfig
    const val BASE_URL = "http://10.0.2.2:8080"

    const val PATH_EMOTIONS         = "/api/emotions"
    const val PATH_SCENARIOS        = "/api/scenarios"
    const val PATH_JOURNAL_ENTRIES  = "/api/journal-entries"
    const val PATH_PRACTICE_ATTEMPTS = "/api/practice-attempts"
    const val PATH_PROGRESS         = "/api/progress"
}
