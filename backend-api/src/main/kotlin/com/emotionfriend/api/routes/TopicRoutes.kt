package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.LessonTopicService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.topicRoutes(service: LessonTopicService) {
    route("/api/topics") {
        // GET /api/topics — list all topics ordered by sort_order
        get {
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = service.getAll()))
        }

        // GET /api/topics/{id} — single topic
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "id must be an integer"),
                )
            val topic = service.getById(id)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = topic))
        }

        // GET /api/topics/{id}/scenarios — all scenarios belonging to this topic
        get("/{id}/scenarios") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "id must be an integer"),
                )
            val scenarios = service.getScenariosForTopic(id)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenarios))
        }
    }
}
