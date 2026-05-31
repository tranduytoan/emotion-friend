package com.emotionfriend.api.model

data class AuthenticatedUser(
    val id: Int,
    val email: String,
    val displayName: String,
    val role: String = "CHILD",
    val isVerified: Boolean = true,
)
