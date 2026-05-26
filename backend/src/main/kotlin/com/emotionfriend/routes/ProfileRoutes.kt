package com.emotionfriend.routes

import com.emotionfriend.models.ProfileUpdateRequest
import com.emotionfriend.services.ProfileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.profileRoutes(service: ProfileService) {
    route("/api/profile") {
        get {
            val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: 1L
            call.respond(service.getProfile(userId))
        }

        put {
            val req = call.receive<ProfileUpdateRequest>()
            call.respond(HttpStatusCode.OK, service.updateProfile(req))
        }
    }
}
