package com.emotionfriend.core.config

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

    /** Backend base URL.  Change this before running on a physical device or VPS. */
    const val BASE_URL = "http://10.0.2.2:80"

    /** TCP connection timeout in milliseconds. */
    const val CONNECT_TIMEOUT_MS: Int = 10_000

    /** Socket (read) timeout in milliseconds. */
    const val SOCKET_TIMEOUT_MS: Int = 30_000
}
