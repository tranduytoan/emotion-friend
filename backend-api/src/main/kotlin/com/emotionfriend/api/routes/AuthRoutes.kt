package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.AuthForgotPasswordRequest
import com.emotionfriend.api.dto.AuthLoginRequest
import com.emotionfriend.api.dto.AuthRegisterRequest
import com.emotionfriend.api.dto.AuthResponseDto
import com.emotionfriend.api.dto.AuthVerifyEmailRequest
import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(service: AuthService) {
    route("/api/auth") {
        post("/login") {
            val request = call.receive<AuthLoginRequest>()
            val user = service.login(request.email, request.password)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = user.toDto("Đăng nhập thành công.")))
        }

        post("/register") {
            val request = call.receive<AuthRegisterRequest>()
            val user = service.register(request.email, request.password, request.displayName)
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = user.toDto("Đăng ký thành công.")))
        }

        post("/forgot-password") {
            val request = call.receive<AuthForgotPasswordRequest>()
            val message = service.forgotPassword(request.email)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = message))
        }

        post("/verify-email") {
            val request = call.receive<AuthVerifyEmailRequest>()
            val user = service.verifyEmail(request.email, request.code)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = user.toDto("Xác thực email thành công.")))
        }
    }
}

private fun AuthenticatedUser.toDto(message: String): AuthResponseDto = AuthResponseDto(
    userId = id.toLong(),
    email = email,
    displayName = displayName,
    role = role,
    token = "",
    message = message,
    isVerified = isVerified,
)
