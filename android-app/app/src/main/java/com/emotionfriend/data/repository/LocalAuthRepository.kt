package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Legacy local fallback for [AuthRepository].
 *
 * This implementation intentionally avoids hardcoded demo users and should only
 * be used when backend auth is unavailable.
 */
@Singleton
class LocalAuthRepository @Inject constructor() : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return Result.failure(Exception("Local auth is disabled. Please use backend authentication."))
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
    ): Result<AuthUser> {
        return Result.failure(Exception("Local auth is disabled. Please use backend authentication."))
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return Result.failure(Exception("Local auth is disabled. Please use backend authentication."))
    }

    override suspend fun verifyEmail(email: String, code: String): Result<AuthUser> {
        return Result.failure(Exception("Local auth is disabled. Please use backend authentication."))
    }
}
