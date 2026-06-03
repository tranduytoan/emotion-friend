package com.emotionfriend.api.service

import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.repository.AuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthServiceTest {
    private class FakeAuthRepo : AuthRepository {
        override suspend fun authenticate(email: String, password: String): AuthenticatedUser? {
            return if (email == "existing@example.com" && password == "password123") {
                AuthenticatedUser(id = 5, email = email, displayName = "Existing User")
            } else {
                null
            }
        }

        override suspend fun findByEmail(email: String): AuthenticatedUser? {
            return if (email == "existing@example.com") {
                AuthenticatedUser(id = 5, email = email, displayName = "Existing User", isVerified = false)
            } else {
                null
            }
        }

        override suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser {
            return AuthenticatedUser(id = 11, email = email, displayName = displayName)
        }
    }

    private val service = AuthService(FakeAuthRepo())

    @Test
    fun `login returns authenticated user for valid credentials`() {
        val user = service.login("existing@example.com", "password123")

        assertEquals(5, user.id)
        assertEquals("existing@example.com", user.email)
        assertEquals("Existing User", user.displayName)
    }

    @Test
    fun `login rejects blank email`() {
        assertFailsWith<IllegalArgumentException> { service.login("", "password123") }
    }

    @Test
    fun `login rejects invalid credentials`() {
        assertFailsWith<IllegalArgumentException> { service.login("existing@example.com", "wrong") }
    }

    @Test
    fun `register rejects already existing email`() {
        assertFailsWith<IllegalArgumentException> {
            service.register("existing@example.com", "password123", "New User")
        }
    }

    @Test
    fun `register returns new authenticated user when email is available`() {
        val created = service.register("new@example.com", "password123", "New User")

        assertEquals(11, created.id)
        assertEquals("new@example.com", created.email)
        assertEquals("New User", created.displayName)
    }

    @Test
    fun `forgotPassword rejects blank email`() {
        assertFailsWith<IllegalArgumentException> { service.forgotPassword("") }
    }

    @Test
    fun `forgotPassword returns reset acknowledgment for valid email`() {
        val message = service.forgotPassword("someone@example.com")

        assertEquals("Yêu cầu đặt lại mật khẩu đã được ghi nhận cho someone@example.com.", message)
    }

    @Test
    fun `verifyEmail rejects invalid code`() {
        assertFailsWith<IllegalArgumentException> { service.verifyEmail("existing@example.com", "000000") }
    }

    @Test
    fun `verifyEmail rejects unknown email`() {
        assertFailsWith<IllegalArgumentException> { service.verifyEmail("missing@example.com", "123456") }
    }

    @Test
    fun `verifyEmail returns verified user on success`() {
        val verified = service.verifyEmail("existing@example.com", "123456")

        assertEquals(5, verified.id)
        assertEquals("existing@example.com", verified.email)
        assertEquals("Existing User", verified.displayName)
        assertEquals(true, verified.isVerified)
    }
}
