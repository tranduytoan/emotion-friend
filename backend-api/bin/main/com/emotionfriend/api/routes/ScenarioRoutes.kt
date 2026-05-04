package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.ScenarioService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.scenarioRoutes(service: ScenarioService) {
    route("/api/scenarios") {
        get {
            val scenarios = service.getAll()
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenarios))
        }
    }
}
