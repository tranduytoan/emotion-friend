package com.emotionfriend.domain.model

/**
 * Represents the authentication state observed by [MainActivity] and
 * [EmotionFriendNavHost] to route the user to the correct destination.
 */
sealed class AuthState {
    /** Session check in progress — show splash/loading. */
    data object Loading : AuthState()

    /** No active session — navigate to LoginScreen. */
    data object Unauthenticated : AuthState()

    /** Email submitted, waiting for verification code. */
    data class AwaitingVerification(val email: String) : AuthState()

    /** Fully authenticated and verified — navigate based on [user.role]. */
    data class Authenticated(val user: AuthUser) : AuthState()
}
