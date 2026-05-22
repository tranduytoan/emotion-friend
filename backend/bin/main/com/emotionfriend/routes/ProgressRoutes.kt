package com.emotionfriend.routes

import com.emotionfriend.services.ProgressService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.progressRoutes(service: ProgressService) {
    route("/api/progress") {
        get {
            // ?userId=1  (defaults to 1 if not provided)
            val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: 1L
            call.respond(service.getByUserId(userId))
        }
    }
}
