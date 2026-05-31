package com.emotionfriend.api.repository

import com.emotionfriend.api.model.AuthenticatedUser

interface AuthRepository {
    suspend fun authenticate(email: String, password: String): AuthenticatedUser?
    suspend fun findByEmail(email: String): AuthenticatedUser?
    suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser
}
