package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.ScenarioService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.scenarioRoutes(service: ScenarioService) {
    route("/api/scenarios") {
        // GET /api/scenarios — list all
        get {
            val scenarios = service.getAll()
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenarios))
        }

        // GET /api/scenarios/{id} — single by ID
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "id is required"),
                )
            val scenario = service.getById(id)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenario))
        }
    }
}
