package com.emotionfriend.routes

import com.emotionfriend.models.ForgotPasswordRequest
import com.emotionfriend.models.LoginRequest
import com.emotionfriend.models.MessageResponse
import com.emotionfriend.models.RegisterRequest
import com.emotionfriend.repositories.AuthRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Routing.authRoutes(authRepo: AuthRepository) {
    route("/api/auth") {

        /**
         * POST /api/auth/login
         * Body: { "email": "...", "password": "..." }
         * 200: AuthResponse (with token)
         * 401: error message
         */
        post("/login") {
            val req = call.receive<LoginRequest>()
            if (req.email.isBlank() || req.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse("Email and password required", false))
                return@post
            }
            val auth = authRepo.login(req.email.trim(), req.password)
            if (auth == null) {
                call.respond(HttpStatusCode.Unauthorized, MessageResponse("Invalid email or password", false))
            } else {
                call.respond(HttpStatusCode.OK, auth)
            }
        }

        /**
         * POST /api/auth/register
         * Body: { "email": "...", "password": "...", "displayName": "...", "role": "CHILD" }
         * 201: AuthResponse
         * 409: duplicate email
         */
        post("/register") {
            val req = call.receive<RegisterRequest>()
            if (req.email.isBlank() || req.password.isBlank() || req.displayName.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse("All fields are required", false))
                return@post
            }
            val validRoles = setOf("CHILD", "PARENT", "THERAPIST")
            if (req.role.uppercase() !in validRoles) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse("Role must be one of: $validRoles", false))
                return@post
            }
            val auth = authRepo.register(req)
            if (auth == null) {
                call.respond(HttpStatusCode.Conflict, MessageResponse("Email already registered", false))
            } else {
                call.respond(HttpStatusCode.Created, auth)
            }
        }

        /**
         * POST /api/auth/forgot-password
         * Body: { "email": "..." }
         * Always returns 200 to avoid email enumeration.
         */
        post("/forgot-password") {
            val req = call.receive<ForgotPasswordRequest>()
            // In production: send reset email via SMTP / SES.
            // Here we just acknowledge the request.
            println("[AuthRoutes] Password reset requested for: ${req.email}")
            call.respond(
                HttpStatusCode.OK,
                MessageResponse("If this email is registered, a reset link has been sent.")
            )
        }
    }
}
