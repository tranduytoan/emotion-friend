package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.StoryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.storyRoutes(service: StoryService) {
    route("/api/stories") {
        // GET /api/stories — list all stories (public, no auth required)
        get {
            val stories = service.getAll()
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = stories))
        }

        // GET /api/stories/{id} — single story by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "id must be an integer"),
                )
            runCatching { service.getById(id) }
                .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                .onFailure {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse<Unit>(success = false, error = it.message),
                    )
                }
        }
    }
}
