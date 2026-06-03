package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.AuthResponseDto
import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.AuthRepository
import com.emotionfriend.api.service.AuthService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRoutesTest {
    private class FakeAuthRepo : AuthRepository {
        override suspend fun authenticate(email: String, password: String): AuthenticatedUser? {
            return if (email == "test@example.com" && password == "ok123") {
                AuthenticatedUser(id = 1, email = email, displayName = "Test User")
            } else {
                null
            }
        }

        override suspend fun findByEmail(email: String): AuthenticatedUser? {
            return if (email == "test@example.com") {
                AuthenticatedUser(id = 1, email = email, displayName = "Test User", isVerified = false)
            } else {
                null
            }
        }

        override suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser {
            return AuthenticatedUser(id = 2, email = email, displayName = displayName)
        }
    }

    private val service = AuthService(FakeAuthRepo())

    @Test
    fun `login endpoint returns success data for valid credentials`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(service) }
        }

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"email": "test@example.com", "password": "ok123"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val result = response.body<ApiResponse<AuthResponseDto>>()
        assertEquals(true, result.success)
        assertEquals("test@example.com", result.data?.email)
        assertEquals("Đăng nhập thành công.", result.data?.message)
    }

    @Test
    fun `login endpoint maps invalid input to bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(service) }
        }

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"email": "", "password": ""}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("Email không được để trống.", result.error)
    }

    @Test
    fun `register endpoint accepts unknown json keys and creates a user`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(service) }
        }

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "email": "new@example.com",
                  "password": "secure",
                  "displayName": "New Player",
                  "extraField": "ignored"
                }
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val result = response.body<ApiResponse<AuthResponseDto>>()
        assertEquals(true, result.success)
        assertEquals("new@example.com", result.data?.email)
        assertEquals("Đăng ký thành công.", result.data?.message)
    }

    @Test
    fun `forgot password endpoint returns reset message`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(service) }
        }

        val response = client.post("/api/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\": \"someone@example.com\"}")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val result = response.body<ApiResponse<String>>()
        assertEquals(true, result.success)
        assertEquals("Yêu cầu đặt lại mật khẩu đã được ghi nhận cho someone@example.com.", result.data)
    }

    @Test
    fun `verify email endpoint returns verified user on correct code`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(service) }
        }

        val response = client.post("/api/auth/verify-email") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"email": "test@example.com", "code": "123456"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val result = response.body<ApiResponse<AuthResponseDto>>()
        assertEquals(true, result.success)
        assertEquals("Xác thực email thành công.", result.data?.message)
    }

    @Test
    fun `register endpoint rejects already existing email`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(service) }
        }

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"email": "test@example.com", "password": "ok123", "displayName": "Existing User"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("Email này đã được đăng ký.", result.error)
    }
}
