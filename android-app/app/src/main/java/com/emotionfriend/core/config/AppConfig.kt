package com.emotionfriend.core.config

/**
 * Centralised application configuration.
 *
 * All network and app-level constants live here so they can be changed in
 * one place.  For a production release these values should come from
 * BuildConfig fields defined in app/build.gradle.kts.
 *
 * Backend URL guide:
 *   • Android emulator  → local Docker Compose : http://10.0.2.2:8081
 *   • Physical device   → local network IP     : http://<machine-ip>:8081
 *   • Production VPS    →                        https://api.emotionfriend.example.com
 *
 * Docker Compose maps host port 8081 → container port 8080.
 */
object AppConfig {

    // ── Network ──────────────────────────────────────────────────────────────

    /** Backend base URL.  Change this before running on a physical device or VPS. */
    const val BASE_URL = "http://10.0.2.2:8081"

    /** TCP connection timeout in milliseconds. */
    const val CONNECT_TIMEOUT_MS: Int = 10_000

    /** Socket (read) timeout in milliseconds. */
    const val SOCKET_TIMEOUT_MS: Int = 30_000
}
