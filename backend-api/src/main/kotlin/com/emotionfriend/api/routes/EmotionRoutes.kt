package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.EmotionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.emotionRoutes(service: EmotionService) {
    route("/api/emotions") {
        // GET /api/emotions — list all
        get {
            val emotions = service.getAll()
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = emotions))
        }

        // GET /api/emotions/{id} — single by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "id must be an integer"),
                )
            val emotion = service.getById(id)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = emotion))
        }
    }
}
