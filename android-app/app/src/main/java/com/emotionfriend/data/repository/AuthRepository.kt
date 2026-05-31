package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole

/**
 * Authentication repository contract.
 *
 * Implementations can be swapped between:
 * - [RemoteAuthRepository] — production path backed by Ktor backend `/api/auth`.
 * - [LocalAuthRepository] — optional offline/mock fallback for local demo.
 *
 * All operations return [Result] so callers can handle errors uniformly with
 * `onSuccess` / `onFailure` without catching exceptions.
 */
interface AuthRepository {

    /**
     * Attempt to log in with [email] and [password].
     *
     * @return [Result.success] with [AuthUser] on success,
     *         [Result.failure] with a descriptive message on failure.
     */
    suspend fun login(email: String, password: String): Result<AuthUser>

    /**
     * Register a new account.
     *
     * @return [Result.success] with [AuthUser] (unverified) on success.
     */
    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
    ): Result<AuthUser>

    /**
     * Request a password-reset email to be sent to [email].
     *
     * @return [Result.success] with a user-facing confirmation message.
     */
    suspend fun forgotPassword(email: String): Result<String>

    /**
     * Verify the email-verification code sent to the user's inbox.
     *
     * @return [Result.success] with the updated, verified [AuthUser].
     */
    suspend fun verifyEmail(email: String, code: String): Result<AuthUser>
}
