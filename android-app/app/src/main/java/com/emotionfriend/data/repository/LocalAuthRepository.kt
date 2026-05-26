package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of [AuthRepository] for local demo / offline mode.
 *
 * Replace this with a remote implementation backed by the Ktor backend
 * `/api/auth` endpoints once the server-side auth is deployed.
 *
 * ### Accepted demo credentials (login):
 * | Email              | Password    | Role       |
 * |--------------------|-------------|------------|
 * | child@demo.com     | Demo@1234   | CHILD      |
 * | parent@demo.com    | Demo@1234   | PARENT     |
 * | therapist@demo.com | Demo@1234   | THERAPIST  |
 *
 * Any other email/password combination returns a login failure.
 * Register always succeeds for any valid input.
 * Email verification code is hardcoded to "123456" in demo mode.
 */
@Singleton
class LocalAuthRepository @Inject constructor() : AuthRepository {

    // ── In-memory store (demo only) ────────────────────────────────────────────
    private val registeredUsers = mutableMapOf<String, MockUser>(
        "child@demo.com"     to MockUser("child@demo.com",     "Demo@1234", "Demo Child",      UserRole.CHILD,      isVerified = true),
        "parent@demo.com"    to MockUser("parent@demo.com",    "Demo@1234", "Demo Parent",     UserRole.PARENT,     isVerified = true),
        "therapist@demo.com" to MockUser("therapist@demo.com", "Demo@1234", "Demo Therapist",  UserRole.THERAPIST,  isVerified = true),
    )

    private data class MockUser(
        val email: String,
        val password: String,
        val displayName: String,
        val role: UserRole,
        val id: String = UUID.randomUUID().toString(),
        val isVerified: Boolean = false,
    )

    // ── AuthRepository impl ────────────────────────────────────────────────────

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        delay(800) // simulate network latency
        val user = registeredUsers[email.lowercase()]
        return when {
            user == null           -> Result.failure(Exception("Email không tồn tại trong hệ thống."))
            user.password != password -> Result.failure(Exception("Mật khẩu không chính xác."))
            else -> Result.success(
                AuthUser(
                    id          = user.id,
                    email       = user.email,
                    displayName = user.displayName,
                    role        = user.role,
                    isVerified  = user.isVerified,
                )
            )
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
    ): Result<AuthUser> {
        delay(800)
        if (registeredUsers.containsKey(email.lowercase())) {
            return Result.failure(Exception("Email này đã được đăng ký."))
        }
        val newUser = MockUser(
            email       = email.lowercase(),
            password    = password,
            displayName = displayName,
            role        = role,
            isVerified  = false,
        )
        registeredUsers[email.lowercase()] = newUser
        return Result.success(
            AuthUser(
                id          = newUser.id,
                email       = newUser.email,
                displayName = newUser.displayName,
                role        = newUser.role,
                isVerified  = false,
            )
        )
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        delay(600)
        return if (registeredUsers.containsKey(email.lowercase())) {
            Result.success("Email hướng dẫn đặt lại mật khẩu đã được gửi tới $email.")
        } else {
            Result.failure(Exception("Email không tồn tại trong hệ thống."))
        }
    }

    override suspend fun verifyEmail(email: String, code: String): Result<AuthUser> {
        delay(500)
        val user = registeredUsers[email.lowercase()]
            ?: return Result.failure(Exception("Email không tồn tại."))
        // Demo: fixed verification code
        if (code != "123456") {
            return Result.failure(Exception("Mã xác thực không đúng. Vui lòng kiểm tra email."))
        }
        val verified = user.copy(isVerified = true)
        registeredUsers[email.lowercase()] = verified
        return Result.success(
            AuthUser(
                id          = verified.id,
                email       = verified.email,
                displayName = verified.displayName,
                role        = verified.role,
                isVerified  = true,
            )
        )
    }
}
