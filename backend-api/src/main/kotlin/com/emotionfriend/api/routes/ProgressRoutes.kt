package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.PracticeService
import com.emotionfriend.api.service.ProgressService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.progressRoutes(service: ProgressService, practiceService: PracticeService) {
    route("/api/progress") {
        // GET /api/progress/{childId} — summary (completed lessons, accuracy, etc.)
        get("/{childId}") {
            val childId = call.parameters["childId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "childId is required"),
                )
            val summary = service.getProgress(childId)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = summary))
        }

        // GET /api/progress/{childId}/history — ordered list of practice attempts
        get("/{childId}/history") {
            val childId = call.parameters["childId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "childId is required"),
                )
            val history = practiceService.getAllByChildId(childId)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = history))
        }
    }
}
