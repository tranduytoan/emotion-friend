package com.emotionfriend.core.navigation

/**
 * Centralised route constants for Emotion Friend.
 *
 * Owner: Nghĩa — all additions to the route table must go through this file.
 *
 * Using a sealed class gives:
 * • Exhaustive `when` expressions — compiler flags unhandled routes.
 * • A single source of truth — no route strings scattered across the codebase.
 * • Safe refactoring — rename a route here and the compiler catches every usage.
 *
 * Screen → owner mapping:
 * ┌──────────────────────┬─────────┐
 * │ Route                │ Owner   │
 * ├──────────────────────┼─────────┤
 * │ Login                │ Nghĩa   │
 * │ Register             │ Nghĩa   │
 * │ ForgotPassword       │ Nghĩa   │
 * │ VerifyEmail          │ Nghĩa   │
 * │ Home                 │ Nghĩa   │
 * │ LearnEmotion         │ Duy     │
 * │ Situation            │ Dũng    │
 * │ ExpressCamera        │ Hiệp    │
 * │ Relax                │ Hiệp    │
 * │ Journal              │ Toàn    │
 * │ Progress             │ Toàn    │
 * └──────────────────────┴─────────┘
 */
sealed class AppRoute(val route: String) {
    // ── Auth flow ─────────────────────────────────────────────────────────────
    data object Login          : AppRoute("login")
    data object Register       : AppRoute("register")
    data object ForgotPassword : AppRoute("forgot_password")
    /** Route includes email argument: "verify_email/{email}" */
    data object VerifyEmail    : AppRoute("verify_email/{email}") {
        fun createRoute(email: String) = "verify_email/$email"
    }

    // ── Main app ──────────────────────────────────────────────────────────────
    data object Home          : AppRoute("home")
    data object LearnEmotion  : AppRoute("learn_emotion")
    data object Situation     : AppRoute("situation")
    data object ExpressCamera : AppRoute("express_camera")
    data object Relax         : AppRoute("relax")
    data object Journal       : AppRoute("journal")
    data object Progress      : AppRoute("progress")
    data object Profile       : AppRoute("profile")
}
