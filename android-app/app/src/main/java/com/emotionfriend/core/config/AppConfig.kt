
package com.emotionfriend.core.config

/**
 * Centralised application configuration.
 *
 * All network and app-level constants live here so they can be changed in
 * one place.
 *
 * Backend URL guide:
 *   • Android emulator  → public backend IP     : http://157.173.127.217:8088
 *   • Physical device   → public backend IP     : http://157.173.127.217:8088
 *
 * This APK always points to the public server; local Docker is not used.
 */
object AppConfig {

    // ── Network ──────────────────────────────────────────────────────────────

    /** Backend URL injected from .env via BuildConfig. */
    val BASE_URL: String
        get() = com.emotionfriend.BuildConfig.BACKEND_URL

    /** TCP connection timeout in milliseconds. */
    const val CONNECT_TIMEOUT_MS: Int = 10_000

    /** Socket (read) timeout in milliseconds. */
    const val SOCKET_TIMEOUT_MS: Int = 30_000

    // ── OpenAI ───────────────────────────────────────────────────────────────

    /** OpenAI API base URL. */
    const val OPENAI_BASE_URL = "https://api.openai.com/v1"

    /** OpenAI API key.
     * For production builds, inject via BuildConfig or a secrets file instead.
     * DO NOT commit real API keys to source control!
     */
    val OPENAI_API_KEY: String
        get() = com.emotionfriend.BuildConfig.OPENAI_API_KEY
}
