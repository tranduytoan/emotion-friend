package com.emotionfriend.core.navigation

/**
 * Centralized route constants for the app.
 *
 * Using a sealed class allows exhaustive `when` expressions and
 * makes adding new routes safer — the compiler will flag missing branches.
 */
sealed class AppRoute(val route: String) {
    data object Home          : AppRoute("home")
    data object LearnEmotion  : AppRoute("learn_emotion")
    data object Situation     : AppRoute("situation")
    data object ExpressCamera : AppRoute("express_camera")
    data object Relax         : AppRoute("relax")
    data object Journal       : AppRoute("journal")
    data object Progress      : AppRoute("progress")
}
