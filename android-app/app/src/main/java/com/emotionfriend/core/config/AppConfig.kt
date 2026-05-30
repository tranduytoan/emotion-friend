
package com.emotionfriend.core.config

import com.emotionfriend.BuildConfig

/**
 * Centralised application configuration.
 *
 * All network and app-level constants live here so they can be changed in
 * one place.  For a production release these values should come from
 * BuildConfig fields defined in app/build.gradle.kts.
 *
 * Backend URL guide:
 *   • Android emulator  → local Docker Compose : http://10.0.2.2:80
 *   • Physical device   → local network IP     : http://<machine-ip>:80
 *   • Production VPS    →                        https://<your-domain.com>
 *
 * Docker Compose: Nginx listens on host port 80 and proxies → backend:8080.
 * To switch URL, change BASE_URL below (or inject via BuildConfig in release builds).
 */
object AppConfig {

    // ── Network ──────────────────────────────────────────────────────────────

    /**
     * Backend base URL — injected from android-app/.env via BuildConfig.
     *   Emulator → Docker Compose : http://10.0.2.2:80  (default)
     *   Physical device LAN       : http://<machine-ip>:80
     *   Production VPS            : https://<your-domain.com>
     * Set BACKEND_URL in android-app/.env to override.
     */
    val BASE_URL: String get() = BuildConfig.BACKEND_URL

    /** TCP connection timeout in milliseconds. */
    const val CONNECT_TIMEOUT_MS: Int = 10_000

    /** Socket (read) timeout in milliseconds. */
    const val SOCKET_TIMEOUT_MS: Int = 30_000

    // ── OpenAI ───────────────────────────────────────────────────────────────

    /** OpenAI API base URL. */
    const val OPENAI_BASE_URL = "https://api.openai.com/v1"

    /**
     * OpenAI API key.
     * For production builds, inject via BuildConfig or a secrets file instead.
     * DO NOT commit real API keys to source control!
     */
    val OPENAI_API_KEY: String
        get() = BuildConfig.OPENAI_API_KEY
}
