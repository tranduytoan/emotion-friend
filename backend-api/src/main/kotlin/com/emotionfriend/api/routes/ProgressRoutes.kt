package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.ProgressService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.progressRoutes(service: ProgressService) {
    route("/api/progress") {
        get("/{childId}") {
            val childId = call.parameters["childId"]
                ?: throw IllegalArgumentException("childId path parameter is required")
            val summary = service.getProgress(childId)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = summary))
        }
    }
}
