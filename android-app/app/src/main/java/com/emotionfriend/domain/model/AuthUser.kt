package com.emotionfriend.domain.model

/**
 * Represents a successfully authenticated user.
 *
 * @param id           Unique user identifier (local UUID for mock; server ID for production).
 * @param email        User email address.
 * @param displayName  User's display name.
 * @param role         Role that controls post-login navigation and feature access.
 * @param isVerified   Whether the email has been verified (controls VerifyEmail screen flow).
 */
data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val isVerified: Boolean = false,
)
